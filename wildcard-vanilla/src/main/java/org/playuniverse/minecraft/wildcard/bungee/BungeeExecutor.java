package org.playuniverse.minecraft.wildcard.bungee;

import java.util.concurrent.Executor;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public final class BungeeExecutor implements Executor {

    private final Plugin plugin;
    private final TaskScheduler scheduler = ProxyServer.getInstance().getScheduler();

    public BungeeExecutor(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(final Runnable command) {
        scheduler.runAsync(plugin, command);
    }

}
