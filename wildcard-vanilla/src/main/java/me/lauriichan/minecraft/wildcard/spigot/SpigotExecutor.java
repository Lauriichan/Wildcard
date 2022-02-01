package me.lauriichan.minecraft.wildcard.spigot;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class SpigotExecutor extends AbstractExecutorService {

    private final Plugin plugin;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    public SpigotExecutor(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(final Runnable command) {
        scheduler.runTaskAsynchronously(plugin, command);
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
