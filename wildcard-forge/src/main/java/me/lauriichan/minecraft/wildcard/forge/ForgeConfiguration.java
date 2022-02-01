package me.lauriichan.minecraft.wildcard.forge;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class ForgeConfiguration {
    public static class Server {

        public final ConfigValue<String> serverName;

        Server(final ForgeConfigSpec.Builder builder) {
            builder.comment("Common config settings").push("common");

            serverName = builder.comment("Your server's name").translation("wildcard.config.common.serverName").define("serverName",
                "Minecraft Server");

            builder.pop();
        }
    }

    private static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;

    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static void register(final ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.SERVER, serverSpec);
    }
}
