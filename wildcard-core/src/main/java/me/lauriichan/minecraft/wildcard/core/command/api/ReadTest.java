package me.lauriichan.minecraft.wildcard.core.command.api;

@FunctionalInterface
public interface ReadTest {

    void test(StringReader reader) throws IllegalArgumentException;

}
