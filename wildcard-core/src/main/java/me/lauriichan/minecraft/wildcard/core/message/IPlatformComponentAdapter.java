package me.lauriichan.minecraft.wildcard.core.message;

public interface IPlatformComponentAdapter<E> {
    
    PlatformComponent create();
    
    E asHandle(PlatformComponent component);
    
    E[] asHandle(PlatformComponent... components);

}
