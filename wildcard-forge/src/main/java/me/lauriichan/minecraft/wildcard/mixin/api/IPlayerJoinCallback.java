package me.lauriichan.minecraft.wildcard.mixin.api;

import com.mojang.authlib.GameProfile;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import net.minecraft.util.text.ITextComponent;

public interface IPlayerJoinCallback {

    void onJoin(Container<ITextComponent> container, GameProfile profile);

}
