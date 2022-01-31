package org.playuniverse.minecraft.wildcard.core.command.api;

public enum CommandState {

    SUCCESS,
    PARTIAL,
    FAILED;

    private String[] aliases;

    protected CommandState setAliases(final String... aliases) {
        this.aliases = aliases;
        return this;
    }

    public String[] getAliases() {
        return aliases;
    }

    public boolean hasConflicts() {
        return aliases != null;
    }

}
