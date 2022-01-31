package me.lauriichan.minecraft.wildcard.core;

import java.io.File;
import java.util.concurrent.Executor;

import me.lauriichan.minecraft.wildcard.core.command.api.base.BaseCommand;
import me.lauriichan.minecraft.wildcard.core.command.api.base.BaseInfo;
import me.lauriichan.minecraft.wildcard.core.command.api.nodes.RootNode;

public interface IWildcardPlugin {

    BaseCommand<?> build(RootNode<BaseInfo> node, String[] aliases);
    
    default void register(BaseCommand<?> command) {
        getCore().getInjections().inject(command);
    }

    default String getId() {
        return getName().toLowerCase();
    }

    String getName();

    File getDataFolder();

    Executor getExecutor();

    WildcardCore getCore();

    ServiceAdapter getService();

    IWildcardAdapter getAdapter();

}
