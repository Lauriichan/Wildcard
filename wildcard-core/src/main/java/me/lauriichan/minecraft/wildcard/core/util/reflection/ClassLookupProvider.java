package me.lauriichan.minecraft.wildcard.core.util.reflection;

import java.util.Map.Entry;

import static me.lauriichan.minecraft.wildcard.core.util.reflection.FakeLookup.FAKE;

import java.util.Optional;

import com.syntaxphoenix.syntaxapi.reflection.ClassCache;

import me.lauriichan.minecraft.wildcard.core.util.Singleton;
import me.lauriichan.minecraft.wildcard.core.util.platform.VersionProvider;
import me.lauriichan.minecraft.wildcard.core.util.reflection.handle.ClassLookup;
import me.lauriichan.minecraft.wildcard.core.util.reflection.handle.ClassLookupCache;

public class ClassLookupProvider {

    public static final String CB_PATH_FORMAT = "org.bukkit.craftbukkit.%s.%s";
    public static final String NMS_PATH_FORMAT = "net.minecraft.server.%s.%s";

    protected final ClassLookupCache cache;
    protected final VersionProvider provider;

    protected final String cbPath;
    protected final String nmsPath;

    private boolean skip = false;

    public ClassLookupProvider() {
        this(new ClassLookupCache());
    }

    public ClassLookupProvider(final ClassLookupCache cache) {
        this.cache = cache;
        this.provider = Singleton.get(VersionProvider.class);
        final String serverString = provider == null ? "" : provider.getServerVersion().toServerString();
        this.cbPath = String.format(CB_PATH_FORMAT, serverString, "%s");
        this.nmsPath = String.format(NMS_PATH_FORMAT, serverString, "%s");
    }

    /*
     * Delete
     */

    public void deleteByName(final String name) {
        cache.delete(name);
    }

    public void deleteByPackage(final String path) {
        final Entry<String, ClassLookup>[] array = cache.entries();
        for (final Entry<String, ClassLookup> entry : array) {
            if (!entry.getValue().getOwner().getPackage().getName().equals(path)) {
                continue;
            }
            cache.delete(entry.getKey());
        }
    }

    /*
     * Skip
     */

    public ClassLookupProvider require(final boolean skip) {
        this.skip = !skip;
        return this;
    }

    public ClassLookupProvider skip(final boolean skip) {
        this.skip = skip;
        return this;
    }

    public boolean skip() {
        return skip;
    }

    /*
     * Reflection
     */

    public ClassLookupCache getReflection() {
        return cache;
    }

    public String getNmsPath() {
        return nmsPath;
    }

    public String getCbPath() {
        return cbPath;
    }

    public ClassLookup createNMSLookup(final String name, final String path) {
        return skip ? FAKE : cache.create(name, getNMSClass(path));
    }

    public ClassLookup createCBLookup(final String name, final String path) {
        return skip ? FAKE : cache.create(name, getCBClass(path));
    }

    public ClassLookup createLookup(final String name, final String path) {
        return skip ? FAKE : cache.create(name, getClass(path));
    }

    public ClassLookup createLookup(final String name, final Class<?> clazz) {
        return skip ? FAKE : cache.create(name, clazz);
    }

    public Optional<ClassLookup> getOptionalLookup(final String name) {
        return cache.get(name);
    }

    public ClassLookup getLookup(final String name) {
        return cache.get(name).orElse(null);
    }

    public Class<?> getNMSClass(final String path) {
        return getClass(String.format(nmsPath, path));
    }

    public Class<?> getCBClass(final String path) {
        return getClass(String.format(cbPath, path));
    }

    public Class<?> getClass(final String path) {
        return ClassCache.getClass(path);
    }

}