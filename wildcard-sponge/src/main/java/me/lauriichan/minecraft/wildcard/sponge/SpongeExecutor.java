package me.lauriichan.minecraft.wildcard.sponge;

import java.util.concurrent.Executor;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.TaskExecutorService;
import org.spongepowered.plugin.PluginContainer;

public class SpongeExecutor implements Executor {

    private final TaskExecutorService service;

    public SpongeExecutor(PluginContainer container) {
        this.service = Sponge.asyncScheduler().executor(container);
    }

    @Override
    public void execute(Runnable command) {
        service.submit(command);
    }

}
