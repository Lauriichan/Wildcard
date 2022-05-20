package me.lauriichan.minecraft.wildcard.sponge;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.SpongeExecutorService;

public class SpongeExecutor extends AbstractExecutorService {

    private final SpongeExecutorService service;

    public SpongeExecutor(PluginContainer container) {
        this.service = Sponge.getScheduler().createAsyncExecutor(container);
    }

    @Override
    public void execute(Runnable command) {
        service.submit(command);
    }

    @Override
    public void shutdown() {}

    @Override
    public List<Runnable> shutdownNow() {
        return Collections.emptyList();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

}
