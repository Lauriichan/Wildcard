package me.lauriichan.minecraft.wildcard.core.data.setting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import com.syntaxphoenix.syntaxapi.json.JsonEntry;
import com.syntaxphoenix.syntaxapi.json.JsonObject;
import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.json.ValueType;
import com.syntaxphoenix.syntaxapi.json.io.JsonSyntaxException;
import com.syntaxphoenix.syntaxapi.logging.ILogger;
import com.syntaxphoenix.syntaxapi.logging.LogTypeId;
import com.syntaxphoenix.syntaxapi.utils.java.Files;

import me.lauriichan.minecraft.wildcard.core.WildcardCore;
import me.lauriichan.minecraft.wildcard.core.data.setting.json.JsonIO;
import me.lauriichan.minecraft.wildcard.core.util.Resources;

public final class Settings {

    private final ConcurrentHashMap<String, ISetting> settings = new ConcurrentHashMap<>();
    private final File file;

    private JsonObject root;

    private final ILogger logger;

    public Settings(final WildcardCore core) {
        this.file = new File(core.getPlugin().getDataFolder(), "settings.json");
        this.logger = core.getLogger();
    }

    public ISetting get(final String compact) {
        return settings.getOrDefault(compact, NullSetting.NULL);
    }

    public void delete(final String compact) {
        final ISetting setting = settings.remove(compact);
        if (setting == null || !(setting instanceof ValueSetting)) {
            return;
        }
        ((ValueSetting) setting).clear();
    }

    public ISetting get(final String name, final Category category) {
        return get(name, category.getName());
    }

    public ISetting get(final String name, final String category) {
        return get(category + '.' + name);
    }

    public ISetting[] getAll(final String category) {
        final Enumeration<String> iterator = settings.keys();
        final ArrayList<ISetting> output = new ArrayList<>();
        while (iterator.hasMoreElements()) {
            final String key = iterator.nextElement();
            if (!key.startsWith(category)) {
                continue;
            }
            output.add(settings.get(key));
        }
        return output.toArray(new ISetting[output.size()]);
    }

    public void loadComplex(final Category category, final Class<?> type) {
        category.load(this, type);
    }

    public void loadComplex(final String category, final Class<?> type) {
        final String categoryKey = '#' + category;
        if (root == null || !root.has(categoryKey, ValueType.OBJECT)) {
            return;
        }
        final JsonObject section = (JsonObject) root.get(categoryKey);
        for (final JsonEntry<?> entry : section.entries()) {
            final ISetting setting = ISetting.of(entry.getKey(), category, type, true);
            put(setting);
            final Object object = JsonIO.toObject(entry.getValue(), type);
            if (object == null) {
                continue;
            }
            setting.set(object);
        }
    }

    public void loadPrimitives(final Category category) {
        category.load(this);
    }

    public void loadPrimitives(final String category) {
        final String categoryKey = '#' + category;
        if (root == null || !root.has(categoryKey, ValueType.OBJECT)) {
            return;
        }
        final JsonObject section = (JsonObject) root.get(categoryKey);
        for (final JsonEntry<?> entry : section.entries()) {
            final JsonValue<?> value = entry.getValue();
            if (!value.getType().isPrimitive()) {
                continue;
            }
            final Class<?> type = value.getValue().getClass();
            final ISetting setting = ISetting.of(entry.getKey(), category, type, true);
            put(setting);
            final Object object = JsonIO.toObject(value, type);
            if (object == null) {
                continue;
            }
            setting.set(object);
        }
    }

    public ISetting put(final ISetting setting) {
        final String compact = setting.asCompact();
        final ISetting current = get(compact);
        if (current.isValid()) {
            return current;
        }
        settings.put(compact, setting);
        return setting;
    }

    public void clear() {
        settings.clear();
    }

    public void load() {
        JsonObject object;
        try {
            if (!file.exists()) {
                Resources.getExternalPathFor("settings.json");
                if (!file.exists()) {
                    throw new IllegalStateException();
                }
            }
            final JsonValue<?> value = JsonIO.PARSER.fromFile(file);
            if (value == null || !value.hasType(ValueType.OBJECT)) {
                return;
            }
            object = (JsonObject) value;
        } catch (IOException | IllegalArgumentException | IllegalStateException e) {
            object = new JsonObject();
        } catch (final JsonSyntaxException exp) {
            logger.log(LogTypeId.WARNING, "Failed to load settings.json!");
            logger.log(LogTypeId.WARNING, "Reason: " + exp.getMessage());
            return;
        }
        this.root = object;
        for (final JsonEntry<?> entry : object.entries()) {
            if (entry.getValue() == null) {
                continue;
            }
            String name = entry.getKey();
            if (!name.startsWith("#")) {
                final ISetting setting = get(name, "*");
                if (setting.isValid() && setting.isPersistent()) {
                    final Object value = JsonIO.toObject(entry.getValue(), setting.getType());
                    if (value == null) {
                        continue;
                    }
                    setting.set(value);
                }
                continue;
            }
            if (!entry.getType().isType(ValueType.OBJECT)) {
                continue;
            }
            name = name.substring(1);
            final JsonObject section = (JsonObject) entry.getValue();
            for (final JsonEntry<?> sectionEntry : section.entries()) {
                final String id = sectionEntry.getKey();
                final ISetting setting = get(id, name);
                if (setting.isValid() && setting.isPersistent()) {
                    final Object value = JsonIO.toObject(entry.getValue(), setting.getType());
                    if (value == null) {
                        continue;
                    }
                    setting.set(value);
                }
                continue;
            }
        }
        if (!file.exists()) {
            save();
        }
    }

    public void save() {
        final ISetting[] settings = this.settings.values().toArray(new ISetting[this.settings.size()]);
        final JsonObject root = new JsonObject();
        for (final ISetting setting : settings) {
            if (!setting.isPersistent() || !setting.isValid()) {
                continue;
            }
            final JsonValue<?> value = JsonIO.fromObject(setting.get());
            if (value.hasType(ValueType.NULL)) {
                continue;
            }
            String category = setting.getCategory();
            if ("*".equals(category)) {
                root.set(setting.getName(), value);
                continue;
            }
            category = '#' + category;
            if (!root.has(category, ValueType.OBJECT)) {
                root.set(category, new JsonObject());
            }
            ((JsonObject) root.get(category)).set(setting.getName(), value);
        }
        try {
            Files.createFile(file);
            JsonIO.WRITER.toFile(root, file);
        } catch (final IOException e) {
            return;
        }
    }

}
