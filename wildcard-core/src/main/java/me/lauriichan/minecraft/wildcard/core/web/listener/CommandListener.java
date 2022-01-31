package me.lauriichan.minecraft.wildcard.core.web.listener;

import com.syntaxphoenix.syntaxapi.event.EventHandler;
import com.syntaxphoenix.syntaxapi.event.EventListener;

import me.lauriichan.minecraft.wildcard.core.web.command.TokenLoginCommand;
import me.lauriichan.minecraft.wildcard.core.web.command.impl.CommandRegisterEvent;

public final class CommandListener implements EventListener {

    @EventHandler
    public void onRegister(final CommandRegisterEvent event) {
        event.add(TokenLoginCommand.class);
    }

}
