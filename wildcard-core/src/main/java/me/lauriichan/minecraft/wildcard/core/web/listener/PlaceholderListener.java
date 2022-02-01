package me.lauriichan.minecraft.wildcard.core.web.listener;

import com.syntaxphoenix.syntaxapi.event.EventHandler;
import com.syntaxphoenix.syntaxapi.event.EventListener;

import me.lauriichan.minecraft.wildcard.core.data.container.api.IDataType;
import me.lauriichan.minecraft.wildcard.core.data.container.nbt.NbtContainer;
import me.lauriichan.minecraft.wildcard.core.settings.Translation;
import me.lauriichan.minecraft.wildcard.core.util.placeholder.Placeholder;
import me.lauriichan.minecraft.wildcard.core.web.util.PageInjectPlaceholderEvent;

public final class PlaceholderListener implements EventListener {

    @EventHandler
    public void onInject(final PageInjectPlaceholderEvent event) {
        final Placeholder[] placeholders = event.placeholderArray();
        final Translation translation = Translation.getDefault();
        for (final Placeholder placeholder : placeholders) {
            if (!placeholder.getKey().startsWith("webpage.")) {
                continue;
            }
            placeholder.setValue(translation.translate(placeholder.getKey()));
        }
        if (event.getData().getPathAsString().contains("success")) {
            final NbtContainer data = event.getSender().getSession().getData();
            if (event.hasPlaceholder("player.name")) {
                event.getPlaceholder("player.name").setValue(data.get("login.success", IDataType.STRING));
            }
            data.remove("login.success");
        }
    }

}
