package me.lauriichan.minecraft.wildcard.core.util.platform;

public final class Version {

    private final int major, minor, patch, revision;

    private final String normal, server;

    public Version() {
        this(0, 0, 0, 0);
    }

    public Version(final int major) {
        this(major, 0, 0, 0);
    }

    public Version(final int major, final int minor) {
        this(major, minor, 0, 0);
    }

    public Version(final int major, final int minor, final int patch) {
        this(major, minor, patch, 0);
    }

    public Version(final int major, final int minor, final int patch, final int revision) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.revision = revision;
        StringBuilder builder = new StringBuilder();
        builder.append(major).append('.').append(minor).append('.').append(patch);
        if (revision != 0) {
            builder.append('.').append(revision);
        }
        this.normal = builder.toString();
        builder = new StringBuilder();
        builder.append('v').append(major).append('_').append(minor).append("_R").append(patch);
        if (revision != 0) {
            builder.append('.').append(revision);
        }
        this.server = builder.toString();
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public int getRevision() {
        return revision;
    }

    public String toServerString() {
        return server;
    }

    @Override
    public String toString() {
        return normal;
    }

    public static Version fromString(String string) {
        final String[] parts = (string = string.replaceFirst("v", "")).contains("_") ? string.split("_")
            : string.contains(".") ? string.split("\\.")
                : new String[] {
                    string
                };
        switch (parts.length) {
        case 1:
            return new Version(parse(parts[0]));
        case 2:
            return new Version(parse(parts[0]), parse(parts[1]));
        case 3:
            if ((parts[2] = parts[2].replaceFirst("R", "")).contains(".")) {
                final String[] parts0 = parts[2].split("\\.");
                return new Version(parse(parts[0]), parse(parts[1]), parse(parts0[0]), parse(parts0[1]));
            }
            return new Version(parse(parts[0]), parse(parts[1]), parse(parts[2]));
        }
        return new Version();
    }

    private static int parse(final String string) {
        try {
            return Integer.parseInt(string);
        } catch (final NumberFormatException ex) {
            return 0;
        }
    }

}
