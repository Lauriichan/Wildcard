package me.lauriichan.minecraft.wildcard.core.util.tick;

@FunctionalInterface
public interface ITickReceiver {

    void onTick(long deltaTime);

}