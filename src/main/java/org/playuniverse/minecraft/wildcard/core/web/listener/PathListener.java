package org.playuniverse.minecraft.wildcard.core.web.listener;

import org.playuniverse.minecraft.wildcard.core.data.container.api.IDataType;
import org.playuniverse.minecraft.wildcard.core.data.container.nbt.NbtContainer;
import org.playuniverse.minecraft.wildcard.core.web.WebSender;
import org.playuniverse.minecraft.wildcard.core.web.util.PathRequestEvent;

import com.syntaxphoenix.syntaxapi.event.EventHandler;
import com.syntaxphoenix.syntaxapi.event.EventListener;

public final class PathListener implements EventListener {

    @EventHandler
    public void onPathRequest(final PathRequestEvent event) {
        final WebSender sender = event.getSender();
        if (!event.getRequest().getPathAsString().contains("success")) {
            return;
        }
        final NbtContainer data = sender.getSession().getData();
        if (!data.has("login.success", IDataType.STRING)) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(!data.get("login.success", IDataType.STRING).isBlank());
    }

}
