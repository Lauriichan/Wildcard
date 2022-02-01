package me.lauriichan.minecraft.wildcard.bungee;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public final class BungeeExecutor extends AbstractExecutorService {

    private final Plugin plugin;
    private final TaskScheduler scheduler = ProxyServer.getInstance().getScheduler();

    public BungeeExecutor(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(final Runnable command) {
        scheduler.runAsync(plugin, command);
    }

    @Override
    public void shutdown() {}

    @Override
    public List<Runnable> shutdownNow() {
        return Collections.emptyList();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

}
