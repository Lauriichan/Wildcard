package me.lauriichan.minecraft.wildcard.core.web.command.impl;

import me.lauriichan.minecraft.wildcard.core.command.api.nodes.RootNode;

public interface ICommand {

    RootNode<WebSource> build(final String name);

}
