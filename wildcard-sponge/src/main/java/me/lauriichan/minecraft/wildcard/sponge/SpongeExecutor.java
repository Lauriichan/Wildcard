package me.lauriichan.minecraft.wildcard.sponge;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.TaskExecutorService;
import org.spongepowered.plugin.PluginContainer;

public class SpongeExecutor extends AbstractExecutorService {

    private final TaskExecutorService service;

    public SpongeExecutor(PluginContainer container) {
        this.service = Sponge.asyncScheduler().executor(container);
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
