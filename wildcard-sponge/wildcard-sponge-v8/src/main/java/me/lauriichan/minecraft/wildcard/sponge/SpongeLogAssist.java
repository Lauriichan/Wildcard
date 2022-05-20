package me.lauriichan.minecraft.wildcard.sponge;

import org.apache.logging.log4j.Logger;

import me.lauriichan.minecraft.wildcard.core.util.ILogAssist;

final class SpongeLogAssist implements ILogAssist {

    private final Logger logger;

    public SpongeLogAssist(WildcardSponge sponge) {
        this.logger = sponge.getLogger();
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

}
