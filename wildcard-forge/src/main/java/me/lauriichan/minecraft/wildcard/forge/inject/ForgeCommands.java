package me.lauriichan.minecraft.wildcard.forge.inject;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import me.lauriichan.minecraft.wildcard.core.util.inject.Injector;
import me.lauriichan.minecraft.wildcard.core.util.reflection.ClassLookupProvider;
import me.lauriichan.minecraft.wildcard.core.util.registry.UniqueRegistry;
import me.lauriichan.minecraft.wildcard.forge.command.ForgeCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public final class ForgeCommands extends Injector<ForgeCommand> {

    private final UniqueRegistry<ForgeCommand> registry = new UniqueRegistry<>();

    @Override
    public Class<ForgeCommand> getType() {
        return ForgeCommand.class;
    }

    @Override
    protected void inject0(final ClassLookupProvider provider, final ForgeCommand transfer) {
        if (transfer == null || registry.isRegistered(transfer.getId())) {
            return;
        }
        LiteralArgumentBuilder<CommandSource> command = ForgeCommandImpl.implementationFor(transfer, transfer.getId());
        if (command == null) {
            System.out.println("No command for '" + transfer.getId() + "'!");
            return;
        }
        CommandDispatcher<CommandSource> dispatcher = ((MinecraftServer) LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER))
            .getCommands().getDispatcher();
        LiteralCommandNode<CommandSource> commandNode = dispatcher.register(command);
        for (String alias : transfer.getAliases()) {
            dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal(alias).redirect(commandNode));
        }
        registry.register(transfer);
    }

    @Override
    protected void uninject0(final ClassLookupProvider provider, final ForgeCommand transfer) {
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
        final ForgeCommand[] array = registry.values().toArray(new ForgeCommand[registry.size()]);
        for (final ForgeCommand transfer : array) {
            uninject0(provider, transfer);
        }
    }

    @Override
    protected void dispose() {
        registry.dispose();
    }

}
