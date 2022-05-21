package me.lauriichan.minecraft.wildcard.sponge;

import me.lauriichan.minecraft.wildcard.core.IWildcardAdapter;
import me.lauriichan.minecraft.wildcard.core.util.ILogAssist;
import me.lauriichan.minecraft.wildcard.sponge.component.SpongeComponentAdapter;

public final class SpongeAdapter implements IWildcardAdapter {

    private final SpongeComponentAdapter componentAdapter = new SpongeComponentAdapter();
    private final ILogAssist assist;

    public SpongeAdapter(WildcardSponge wildcard) {
        this.assist = new SpongeLogAssist(wildcard);
    }
    
    @Override
    public SpongeComponentAdapter getComponentAdapter() {
        return componentAdapter;
    }

    @Override
    public ILogAssist getLogAssist() {
        return assist;
    }

}
