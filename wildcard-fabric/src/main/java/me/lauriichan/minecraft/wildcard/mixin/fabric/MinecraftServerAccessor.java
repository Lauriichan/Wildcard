package me.lauriichan.minecraft.wildcard.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.lauriichan.minecraft.wildcard.mixin.api.FabricMixin;
import me.lauriichan.minecraft.wildcard.mixin.api.IMinecraftServerAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;

@Environment(EnvType.SERVER)
@Mixin(MinecraftDedicatedServer.class)
public abstract class MinecraftServerAccessor implements IMinecraftServerAccess {
    

    @Inject(method = "<init>", at = @At("INVOKE"))
    private void initialize(CallbackInfo info) {
        FabricMixin.SERVER.replace(this).lock();
    }

}
