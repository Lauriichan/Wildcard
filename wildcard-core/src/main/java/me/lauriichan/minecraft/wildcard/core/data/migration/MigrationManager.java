package me.lauriichan.minecraft.wildcard.core.data.migration;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;
import com.syntaxphoenix.syntaxapi.reflection.ClassCache;

import me.lauriichan.minecraft.wildcard.core.util.DynamicArray;
import me.lauriichan.minecraft.wildcard.core.util.InstanceCreator;
import me.lauriichan.minecraft.wildcard.core.util.ReflectHelper;
import me.lauriichan.minecraft.wildcard.core.util.Singleton;
import me.lauriichan.minecraft.wildcard.core.util.registry.Registry;
import me.lauriichan.minecraft.wildcard.core.util.source.PathSource;
import me.lauriichan.minecraft.wildcard.migration.IMigrationManager;
import me.lauriichan.minecraft.wildcard.migration.IMigrationSource;
import me.lauriichan.minecraft.wildcard.migration.Migration;
import me.lauriichan.minecraft.wildcard.migration.MigrationProvider;
import me.lauriichan.minecraft.wildcard.migration.MigrationTarget;
import me.lauriichan.minecraft.wildcard.migration.MigrationType;

public final class MigrationManager implements IMigrationManager {

    private final DynamicArray<MigrationTarget<?>> migrations = new DynamicArray<>();
    private final Registry<Class<?>, MigrationType<?, ?>> types = new Registry<>();

    public MigrationManager() {
        ILogger logger = Singleton.get(ILogger.class);
        try (BufferedReader reader = PathSource.ofResource("META-INF/migrations").openReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                Class<?> clazz = ClassCache.getClass(line);
                if (clazz == null) {
                    logger.log(LogTypeId.WARNING, "Couldn't find migration class '" + line + "'!");
                    continue;
                }
                if (MigrationProvider.class.isAssignableFrom(clazz)) {
                    logger.log(LogTypeId.WARNING, "Migration class '" + clazz.getSimpleName() + "' is not a MigrationProvider!");
                    continue;
                }
                Optional<Migration> option = ReflectHelper.getAnnotation(clazz, Migration.class);
                if (!option.isPresent()) {
                    logger.log(LogTypeId.WARNING, "No Migration annotation found at '" + clazz.getSimpleName() + "'!");
                    continue;
                }
                Migration migration = option.get();
                Class<?> source = migration.source();
                Class<?> typeClazz = migration.type();
                if (source == null || typeClazz == null) {
                    logger.log(LogTypeId.WARNING, "Source or Type of migration '" + clazz.getSimpleName() + "' is not defined!");
                    continue;
                }
                MigrationType<?, ?> type = types.get(typeClazz);
                if (!types.isRegistered(typeClazz)) {
                    try {
                        type = InstanceCreator.create(typeClazz.asSubclass(MigrationType.class), Singleton.getInjects());
                    } catch (Exception e) {
                        logger.log(LogTypeId.WARNING, "Failed to create instance of migration type '" + typeClazz.getSimpleName() + "'!");
                        logger.log(LogTypeId.WARNING, e);
                        continue;
                    }
                    types.register(typeClazz, type);
                }
                if (!type.getSource().isAssignableFrom(source)) {
                    logger.log(LogTypeId.WARNING, "Source of migration '" + clazz.getSimpleName() + "' is not supported by migration type '"
                        + typeClazz.getSimpleName() + "'!");
                    continue;
                }
                if (!type.getMigration().isAssignableFrom(clazz)) {
                    logger.log(LogTypeId.WARNING, "MigrationProvider type of '" + clazz.getSimpleName()
                        + "' is not supported by migration type '" + typeClazz.getSimpleName() + "'!");
                    continue;
                }
                MigrationProvider provider;
                try {
                    provider = InstanceCreator.create(clazz.asSubclass(MigrationProvider.class), Singleton.getInjects());
                } catch (Exception e) {
                    logger.log(LogTypeId.WARNING, "Failed to create instance of migration '" + clazz.getSimpleName() + "'!");
                    logger.log(LogTypeId.WARNING, e);
                    continue;
                }
                migrations.add(new MigrationTarget<>(migration, provider));
            }
        } catch (IOException exp) {
            logger.log(LogTypeId.ERROR, "Failed to load migrations!");
            logger.log(LogTypeId.ERROR, exp);
        }
    }

    public <T extends MigrationType<?, ?>> T getType(Class<T> type) {
        if (type == null) {
            return null;
        }
        if (!types.isRegistered(type)) {
            T migrationType;
            try {
                migrationType = InstanceCreator.create(type, Singleton.getInjects());
            } catch (Exception e) {
                ILogger logger = Singleton.get(ILogger.class);
                logger.log(LogTypeId.WARNING, "Failed to create instance of migration type '" + type.getSimpleName() + "'!");
                logger.log(LogTypeId.WARNING, e);
                return null;
            }
            types.register(type, migrationType);
            return migrationType;
        }
        return type.cast(types.get(type));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <M extends MigrationProvider> List<MigrationTarget<M>> getTargets(Class<MigrationType<?, M>> type) {
        ArrayList<MigrationTarget<M>> list = new ArrayList<>();
        for (int index = 0; index < migrations.length(); index++) {
            MigrationTarget<?> target = migrations.get(index);
            if (!target.getPoint().type().equals(type)) {
                continue;
            }
            list.add((MigrationTarget<M>) target);
        }
        return list;
    }

    public static <S extends IMigrationSource, T extends MigrationType<S, ?>> void migrate(IMigrationSource source, Class<T> type)
        throws Exception {
        MigrationManager manager = Singleton.get(MigrationManager.class);
        T migration = manager.getType(Objects.requireNonNull(type));
        if (migration == null) {
            throw new IllegalStateException("Can't find migration type '" + type.getSimpleName() + "'!");
        }
        if (migration.getSource().isAssignableFrom(source.getClass())) {
            throw new IllegalArgumentException("migration source '" + source.getClass().getSimpleName()
                + "' is not compatible with migration type '" + type.getSimpleName() + "'!");
        }
        migration.migrate(manager, migration.getSource().cast(source));
    }

}
