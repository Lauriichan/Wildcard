package org.playuniverse.minecraft.wildcard.core.command;

import org.playuniverse.minecraft.wildcard.core.command.api.base.BaseInfo;
import org.playuniverse.minecraft.wildcard.core.command.api.nodes.RootNode;

public interface IBasicCommand {

    RootNode<BaseInfo> build(String name);

}
