package me.lauriichan.minecraft.wildcard.core.command;

import me.lauriichan.minecraft.wildcard.core.command.api.base.IPermission;

public enum Permission implements IPermission {

    COMMAND_GET_OTHER(1),
    COMMAND_HISTORY(2),
    COMMAND_RELOAD(3),
    COMMAND_CREATE(4),
    COMMAND_ALLOW(3),
    COMMAND_DENY(3);

    private final String id;
    private final int level;

    Permission(int level) {
        this.id = "wildcard." + name().toLowerCase().replace('_', '.');
        this.level = level;
    }

    @Override
    public String id() {
        return id;
    }
    
    public int level() {
        return level;
    }

}
