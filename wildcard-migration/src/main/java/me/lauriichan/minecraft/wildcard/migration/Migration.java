package me.lauriichan.minecraft.wildcard.migration;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
public @interface Migration {

    Class<? extends IMigrationSource> source();

    Class<? extends MigrationType<?, ?>> type();

}
