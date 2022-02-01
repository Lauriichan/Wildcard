package me.lauriichan.minecraft.wildcard.core.web.listener;

import com.syntaxphoenix.syntaxapi.event.EventHandler;
import com.syntaxphoenix.syntaxapi.event.EventListener;

import me.lauriichan.minecraft.wildcard.core.data.container.api.IDataType;
import me.lauriichan.minecraft.wildcard.core.data.container.nbt.NbtContainer;
import me.lauriichan.minecraft.wildcard.core.web.WebSender;
import me.lauriichan.minecraft.wildcard.core.web.util.PathRequestEvent;

public final class PathListener implements EventListener {

    @EventHandler
    public void onPathRequest(final PathRequestEvent event) {
        final WebSender sender = event.getSender();
        if (!event.getType().has("html") || !event.getRequest().getPathAsString().contains("success")) {
            return;
        }
        final NbtContainer data = sender.getSession().getData();
        if (!data.has("login.success", IDataType.STRING)) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(data.get("login.success", IDataType.STRING).trim().isEmpty());
    }

}
