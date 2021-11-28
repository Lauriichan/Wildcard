package org.playuniverse.minecraft.wildcard.core.web.command.impl;

import org.playuniverse.minecraft.wildcard.core.command.api.nodes.RootNode;

public interface ICommand {

    RootNode<WebSource> build(final String name);

}
