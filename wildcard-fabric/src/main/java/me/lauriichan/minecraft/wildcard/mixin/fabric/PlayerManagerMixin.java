package me.lauriichan.minecraft.wildcard.mixin.fabric;

import java.net.SocketAddress;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.authlib.GameProfile;
import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import me.lauriichan.minecraft.wildcard.mixin.api.FabricMixin;
import me.lauriichan.minecraft.wildcard.mixin.api.IPlayerJoinCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@Environment(EnvType.SERVER)
@Mixin(PlayerManager.class)
abstract class PlayerManagerMixin {

    @Inject(method = "checkCanJoin", at = @At("HEAD"), cancellable = true)
    private void triggerJoinClassback(SocketAddress address, GameProfile profile, CallbackInfoReturnable<Text> info) {
        IPlayerJoinCallback callback = FabricMixin.JOIN_CALLBACK.get();
        if (callback == null) {
            return;
        }
        Container<MutableText> output = Container.of();
        callback.onJoin(output, profile);
        if (output.isEmpty()) {
            return;
        }
        info.setReturnValue(output.get());
    }

}
