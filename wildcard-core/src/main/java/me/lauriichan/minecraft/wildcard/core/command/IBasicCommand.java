package me.lauriichan.minecraft.wildcard.core.command;

import me.lauriichan.minecraft.wildcard.core.command.api.base.BaseInfo;
import me.lauriichan.minecraft.wildcard.core.command.api.nodes.RootNode;

public interface IBasicCommand {

    RootNode<BaseInfo> build(String name);

}
