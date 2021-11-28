package org.playuniverse.minecraft.wildcard.core.web.listener;

import org.playuniverse.minecraft.wildcard.core.web.command.TokenLoginCommand;
import org.playuniverse.minecraft.wildcard.core.web.command.impl.CommandRegisterEvent;

import com.syntaxphoenix.syntaxapi.event.EventHandler;
import com.syntaxphoenix.syntaxapi.event.EventListener;

public final class CommandListener implements EventListener {

    @EventHandler
    public void onRegister(final CommandRegisterEvent event) {
        event.add(TokenLoginCommand.class);
    }

}
