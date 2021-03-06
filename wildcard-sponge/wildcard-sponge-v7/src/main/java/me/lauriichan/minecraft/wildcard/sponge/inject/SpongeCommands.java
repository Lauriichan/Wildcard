package me.lauriichan.minecraft.wildcard.sponge.inject;

import java.util.ArrayList;
import java.util.Collections;

import org.spongepowered.api.Sponge;

import me.lauriichan.minecraft.wildcard.core.util.inject.Injector;
import me.lauriichan.minecraft.wildcard.core.util.reflection.ClassLookupProvider;
import me.lauriichan.minecraft.wildcard.core.util.registry.UniqueRegistry;
import me.lauriichan.minecraft.wildcard.sponge.WildcardSponge;
import me.lauriichan.minecraft.wildcard.sponge.command.SpongeCommand;

public class SpongeCommands extends Injector<SpongeCommand> {

    private final UniqueRegistry<SpongeCommand> registry = new UniqueRegistry<>();

    @Override
    public Class<SpongeCommand> getType() {
        return SpongeCommand.class;
    }

    @Override
    protected void inject0(final ClassLookupProvider provider, final SpongeCommand transfer) {
        if (transfer == null || registry.isRegistered(transfer.getId())) {
            return;
        }
        ArrayList<String> names = new ArrayList<>();
        Collections.addAll(names, transfer.getAliases());
        names.add(transfer.getName());
        Sponge.getCommandManager().register(((WildcardSponge) transfer.getCore().getPlugin()).getContainer(), transfer, names);
        registry.register(transfer);
    }

    @Override
    protected void uninject0(final ClassLookupProvider provider, final SpongeCommand transfer) {
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
        final SpongeCommand[] array = registry.values().toArray(new SpongeCommand[registry.size()]);
        for (final SpongeCommand transfer : array) {
            uninject0(provider, transfer);
        }
    }

    @Override
    protected void dispose() {
        registry.dispose();
    }

}