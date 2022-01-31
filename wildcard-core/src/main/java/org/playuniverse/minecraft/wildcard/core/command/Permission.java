package org.playuniverse.minecraft.wildcard.core.command;

import org.playuniverse.minecraft.wildcard.core.command.api.base.IPermission;

public enum Permission implements IPermission {

    COMMAND_GET_OTHER,
    COMMAND_HISTORY,
    COMMAND_RELOAD,
    COMMAND_CREATE,
    COMMAND_ALLOW,
    COMMAND_DENY;

    private final String id;

    Permission() {
        this.id = "wildcard." + name().toLowerCase().replace('_', '.');
    }

    @Override
    public String id() {
        return id;
    }

}
