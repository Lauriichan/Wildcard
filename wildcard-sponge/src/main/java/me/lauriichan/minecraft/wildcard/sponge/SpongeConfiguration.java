package me.lauriichan.minecraft.wildcard.sponge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import com.syntaxphoenix.syntaxapi.utils.java.Files;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import me.lauriichan.minecraft.wildcard.core.util.source.PathSource;

public final class SpongeConfiguration {

    private final Container<String> serverName = Container.of("Minecraft Server");

    private final PathSource source;

    private final Container<String> defaultPath = Container.of();

    public SpongeConfiguration(Path configPath) {
        this.source = new PathSource(configPath);
    }

    public SpongeConfiguration(Path configPath, String defaultPath) {
        this.source = new PathSource(configPath);
        this.defaultPath.replace(defaultPath);
    }
    
    public PathSource getSource() {
        return source;
    }

    public void setDefault(String defaultPath) {
        this.defaultPath.replace(defaultPath);
    }

    public boolean hasDefault() {
        return defaultPath.isPresent();
    }

    public String getDefault() {
        return defaultPath.get();
    }

    private void loadDefault0() {
        PathSource resource = PathSource.ofResource(defaultPath.get());
        File file = source.getSource().toFile();
        Files.createFile(file);
        try (FileOutputStream stream = new FileOutputStream(file)) {
            try (InputStream input = resource.openStream()) {
                byte[] buf = new byte[1024];
                int length = 0;
                while ((length = input.read(buf)) != -1) {
                    stream.write(buf, 0, length);
                }
            }
        } catch (IOException e) {
            // If we fail, then we fail
        }
    }

    public void reload() {
        if (!source.exists()) {
            if (defaultPath.isEmpty()) {
                return; // Don't load anything
            }
            loadDefault0();
        }
        try {
            load(HoconConfigurationLoader.builder().source(source::openReader).build().load());
        } catch (ConfigurateException e) {
            // Failed but we don't really care
        }
    }

    private void load(CommentedConfigurationNode config) {
        serverName.replace(config.node("serverName").getString(serverName.get())); // Just use set name as default value
    }

    /*
     * Getter
     */

    public String getServerName() {
        return serverName.get();
    }

}
