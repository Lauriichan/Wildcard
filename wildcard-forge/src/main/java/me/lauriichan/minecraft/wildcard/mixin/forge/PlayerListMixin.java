package me.lauriichan.minecraft.wildcard.mixin.forge;

import java.net.SocketAddress;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import me.lauriichan.minecraft.wildcard.mixin.api.ForgeMixin;
import me.lauriichan.minecraft.wildcard.mixin.api.IPlayerJoinCallback;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mixin(PlayerList.class)
abstract class PlayerListMixin {

    @Inject(method = "canPlayerLogin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/util/text/ITextComponent;", at = @At("HEAD"), cancellable = true)
    private void triggerJoinClassback(SocketAddress address, GameProfile profile, CallbackInfoReturnable<ITextComponent> info) {
        IPlayerJoinCallback callback = ForgeMixin.JOIN_CALLBACK.get();
        if (callback == null) {
            return;
        }
        Container<ITextComponent> output = Container.of();
        callback.onJoin(output, profile);
        if (output.isEmpty()) {
            return;
        }
        info.setReturnValue(output.get());
    }

}
