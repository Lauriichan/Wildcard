package org.playuniverse.minecraft.wildcard.core;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import org.playuniverse.minecraft.wildcard.core.command.api.base.BaseCommand;
import org.playuniverse.minecraft.wildcard.core.command.api.base.BaseInfo;
import org.playuniverse.minecraft.wildcard.core.command.api.nodes.RootNode;

public interface IWildcardPlugin {

    BaseCommand<?> build(RootNode<BaseInfo> node, String[] aliases);

    default String getId() {
        return getName().toLowerCase();
    }

    String getName();

    Logger getLogger();

    File getDataFolder();

    Executor getExecutor();

    WildcardCore getCore();

    ServiceAdapter getService();

    IWildcardAdapter getAdapter();

}
