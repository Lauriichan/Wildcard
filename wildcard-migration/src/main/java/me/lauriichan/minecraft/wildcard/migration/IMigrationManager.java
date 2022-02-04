package me.lauriichan.minecraft.wildcard.migration;

import java.util.List;

public interface IMigrationManager {
    
    <M extends MigrationProvider> List<MigrationTarget<M>> getTargets(Class<MigrationType<?, M>> type);

}
