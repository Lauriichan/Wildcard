package me.lauriichan.minecraft.wildcard.spigot;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.lauriichan.minecraft.wildcard.core.util.ILogAssist;

final class SpigotLogAssist implements ILogAssist {

    private final CommandSender console = Bukkit.getConsoleSender();

    @Override
    public void info(final String message) {
        console.sendMessage(message);
    }

}
