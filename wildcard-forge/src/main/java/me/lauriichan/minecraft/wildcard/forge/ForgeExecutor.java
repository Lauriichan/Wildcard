package me.lauriichan.minecraft.wildcard.forge;

import java.util.concurrent.Executor;

import com.syntaxphoenix.syntaxapi.utils.java.tools.Container;

import net.minecraft.util.concurrent.ThreadTaskExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

public class ForgeExecutor implements Executor {

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

}
