package org.playuniverse.minecraft.wildcard.spigot;

import java.util.concurrent.Executor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class SpigotExecutor implements Executor {

    private final Plugin plugin;
    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    public SpigotExecutor(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(final Runnable command) {
        scheduler.runTaskAsynchronously(plugin, command);
    }

}
