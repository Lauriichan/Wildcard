package me.lauriichan.minecraft.wildcard.forge;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class ForgeExecutor extends AbstractExecutorService {

    private final Container<ThreadTaskExecutor<?>> executor = Container.of();

    private ThreadTaskExecutor<?> getExecutor() {
        if (executor.isPresent()) {
            return executor.get();
        }
        try {
            return executor.replace(LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER)).get();
        } catch (Exception ignore) {
            return null;
        }
    }

    @Override
    public void execute(Runnable command) {
        ThreadTaskExecutor<?> exe = getExecutor();
        if (exe == null) {
            return;
        }
        exe.submitAsync(command);
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
