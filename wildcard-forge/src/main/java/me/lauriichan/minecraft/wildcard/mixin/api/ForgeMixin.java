package me.lauriichan.minecraft.wildcard.mixin.api;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

public class ForgeMixin {

    public static final Container<IPlayerJoinCallback> JOIN_CALLBACK = Container.of();

    private ForgeMixin() {
        throw new UnsupportedOperationException("Constant class");
    }

}
