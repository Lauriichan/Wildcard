package me.lauriichan.minecraft.wildcard.fabric.inject;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import me.lauriichan.minecraft.wildcard.core.util.inject.Injector;
import me.lauriichan.minecraft.wildcard.core.util.reflection.ClassLookupProvider;
import me.lauriichan.minecraft.wildcard.core.util.registry.UniqueRegistry;
import me.lauriichan.minecraft.wildcard.fabric.command.FabricCommand;
import me.lauriichan.minecraft.wildcard.mixin.api.FabricMixin;
import net.minecraft.server.command.ServerCommandSource;

public final class FabricCommands extends Injector<FabricCommand> {

    private final UniqueRegistry<FabricCommand> registry = new UniqueRegistry<>();

    @Override
    public Class<FabricCommand> getType() {
        return FabricCommand.class;
    }

    @Override
    protected void inject0(final ClassLookupProvider provider, final FabricCommand transfer) {
        if (transfer == null || registry.isRegistered(transfer.getId())) {
            return;
        }
        LiteralArgumentBuilder<ServerCommandSource> command = FabricCommandImpl.implementationFor(transfer, transfer.getId());
        if (command == null) {
            System.out.println("No command for '" + transfer.getId() + "'!");
            return;
        }
        CommandDispatcher<ServerCommandSource> dispatcher = FabricMixin.server().getCommandManager().getDispatcher();
        LiteralCommandNode<ServerCommandSource> commandNode = dispatcher.register(command);
        for (String alias : transfer.getAliases()) {
            dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal(alias).redirect(commandNode));
        }
        registry.register(transfer);
    }

    @Override
    protected void uninject0(final ClassLookupProvider provider, final FabricCommand transfer) {
        if (transfer == null || transfer.getId() == null || !registry.isRegistered(transfer.getId())) {
            return;
        }
        registry.unregister(transfer.getId());
    }

    @Override
    protected void uninjectAll0(final ClassLookupProvider provider) {
        if (registry.isEmpty()) {
            return;
        }
        final FabricCommand[] array = registry.values().toArray(new FabricCommand[registry.size()]);
        for (final FabricCommand transfer : array) {
            uninject0(provider, transfer);
        }
    }

    @Override
    protected void dispose() {
        registry.dispose();
    }

}
