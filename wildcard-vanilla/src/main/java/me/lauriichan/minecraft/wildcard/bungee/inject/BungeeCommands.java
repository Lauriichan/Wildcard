package me.lauriichan.minecraft.wildcard.bungee.inject;

import me.lauriichan.minecraft.wildcard.bungee.command.BungeeCommand;
import me.lauriichan.minecraft.wildcard.bungee.command.PluginCommand;
import me.lauriichan.minecraft.wildcard.core.util.inject.Injector;
import me.lauriichan.minecraft.wildcard.core.util.reflection.ClassLookupProvider;
import me.lauriichan.minecraft.wildcard.core.util.registry.Registry;
import me.lauriichan.minecraft.wildcard.core.util.registry.UniqueRegistry;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class BungeeCommands extends Injector<BungeeCommand> {

    private final UniqueRegistry<BungeeCommand> registry = new UniqueRegistry<>();
    private final Registry<BungeeCommand, PluginCommand> commands = new Registry<>();

    private final PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();

    @Override
    public Class<BungeeCommand> getType() {
        return BungeeCommand.class;
    }

    @Override
    protected void inject0(final ClassLookupProvider provider, final BungeeCommand transfer) {
        if (transfer == null || registry.isRegistered(transfer.getId())) {
            return;
        }
        final PluginCommand command = new PluginCommand(transfer);
        pluginManager.registerCommand((Plugin) transfer.getCore().getPlugin(), command);
        registry.register(transfer);
        commands.register(transfer, command);
    }

    @Override
    protected void uninject0(final ClassLookupProvider provider, final BungeeCommand transfer) {
        if (transfer == null || transfer.getId() == null || !registry.isRegistered(transfer.getId())) {
            return;
        }
        pluginManager.unregisterCommand(commands.get(transfer));
        registry.unregister(transfer.getId());
        commands.unregister(transfer);
    }

    @Override
    protected void uninjectAll0(final ClassLookupProvider provider) {
        if (registry.isEmpty()) {
            return;
        }
        final BungeeCommand[] array = registry.values().toArray(new BungeeCommand[registry.size()]);
        for (final BungeeCommand transfer : array) {
            uninject0(provider, transfer);
        }
    }

    @Override
    protected void dispose() {
        registry.dispose();
        commands.dispose();
    }

}