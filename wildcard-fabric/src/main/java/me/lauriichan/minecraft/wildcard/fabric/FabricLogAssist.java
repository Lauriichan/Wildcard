package me.lauriichan.minecraft.wildcard.fabric;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.lauriichan.minecraft.wildcard.core.util.ILogAssist;

final class FabricLogAssist implements ILogAssist {

    private final Logger logger = LogManager.getLogger(WildcardFabric.class);;

    @Override
    public void info(String message) {
        logger.info(message);
    }

}
