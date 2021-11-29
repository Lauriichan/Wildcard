package org.playuniverse.minecraft.wildcard.core.settings;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.apache.commons.io.file.PathUtils;
import org.playuniverse.minecraft.wildcard.core.data.setting.Serialize;
import org.playuniverse.minecraft.wildcard.core.data.setting.json.JsonIO;
import org.playuniverse.minecraft.wildcard.core.util.ComponentParser;
import org.playuniverse.minecraft.wildcard.core.util.Resources;
import org.playuniverse.minecraft.wildcard.core.util.Singleton;

import com.syntaxphoenix.syntaxapi.json.JsonValue;
import com.syntaxphoenix.syntaxapi.utils.java.Exceptions;
import com.syntaxphoenix.syntaxapi.utils.java.Streams;

import net.md_5.bungee.api.chat.TextComponent;

public final class Translation {

    private static final ArrayList<Translation> TRANSLATIONS = new ArrayList<>();
    private static final Translation EMPTY = new Translation(null);
    private static final PluginSettings SETTINGS = Singleton.get(PluginSettings.class);

    public static void load() {
        if (!TRANSLATIONS.isEmpty()) {
            TRANSLATIONS.clear();
        }
        try {
            final Path root = Resources.getExternalPathFor("translation");
            final Stream<Path> walk = Files.walk(root, 1);
            for (final Iterator<Path> iterator = walk.iterator(); iterator.hasNext();) {
                final Path path = iterator.next();
                if (PathUtils.isDirectory(path)) {
                    continue;
                }
                final String content = Streams.toString(path.getFileSystem().provider().newInputStream(path, StandardOpenOption.READ));
                final JsonValue<?> value = JsonIO.PARSER.fromString(content);
                final Object object = JsonIO.toObject(value, Translation.class);
                if (object == null || !(object instanceof Translation)) {
                    continue;
                }
                TRANSLATIONS.add((Translation) object);
            }
            walk.close();
        } catch (final Exception exp) {
            System.err.println("Failed to load translations");
            System.err.println(Exceptions.stackTraceToString(exp));
        }
    }

    public static String getDefaultCode() {
        final String code = SETTINGS.getString("language");
        return code != null && has(code) ? code : "en-uk";
    }

    public static void setDefaultCode(final String name) {
        SETTINGS.setString("language", name);
    }

    public static Translation getDefault() {
        final Translation translation = get(getDefaultCode());
        return translation == null ? EMPTY : translation;
    }

    public static void setDefault(final Translation project) {
        setDefaultCode(project.getCode());
    }

    public static Translation get(final String name) {
        for (int index = 0; index < TRANSLATIONS.size(); index++) {
            final Translation translation = TRANSLATIONS.get(index);
            if (translation.getCode().equalsIgnoreCase(name) || translation.getName().equalsIgnoreCase(name)) {
                return translation;
            }
        }
        return null;
    }

    public static boolean has(final String name) {
        return get(name) != null;
    }

    @Serialize
    private final String code;
    @Serialize
    private final String name;
    @Serialize
    private final TranslationMap<String, String> keys = new TranslationMap<>();

    public Translation() {
        name = null;
        code = null;
    }

    private Translation(final Object ignore) {
        this.code = "";
        this.name = "";
    }

    public String translate(String text) {
        text = text.replace("$prefix", "$plugin.prefix");
        int count = 0;
        for (final Entry<String, String> entry : keys.entrySet()) {
            if (!text.contains(entry.getKey())) {
                continue;
            }
            text = text.replace(entry.getKey(), entry.getValue());
            count++;
        }
        return count == 0 ? text : translate(text);
    }

    public String translate(final String text, final Object... placeholders) {
        String output = translate(text);
        int changes = 0;
        for (int index = 0; index < placeholders.length; index += 2) {
            final String key = "$" + placeholders[index].toString();
            if (index + 1 >= placeholders.length) {
                break;
            }
            if (!output.contains(key)) {
                continue;
            }
            output = output.replace(key, placeholders[index + 1].toString());
            changes++;
        }
        return changes == 0 ? output : translate(output, placeholders);
    }

    public TextComponent[] translateComponent(final ComponentParser parser, final String text) {
        return parser.parse(translate(text));
    }

    public TextComponent[] translateComponent(final ComponentParser parser, final String text, final Object... placeholders) {
        return parser.parse(translate(text, placeholders));
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

}
