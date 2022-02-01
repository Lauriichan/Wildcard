package me.lauriichan.minecraft.wildcard.forge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.lauriichan.minecraft.wildcard.core.util.ILogAssist;

final class ForgeLogAssist implements ILogAssist {

    private final Logger logger = LogManager.getLogger(WildcardForge.class);;

    @Override
    public void info(String message) {
        logger.info(message);
    }

}
