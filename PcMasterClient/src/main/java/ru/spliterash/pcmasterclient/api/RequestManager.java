package ru.spliterash.pcmasterclient.api;

import org.reflections.Reflections;
import ru.spliterash.pcmasterclient.api.methods.AbstractExecutor;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestManager {
    private static final RequestManager instance = new RequestManager();
    private final Set<AbstractExecutor<?, ?>> executors = new HashSet<>();
    private ExecutorService service = Executors.newCachedThreadPool();

    @SuppressWarnings("rawtypes")
    private RequestManager() {
        Set<Class<? extends AbstractExecutor>> set = new Reflections(getClass().getPackage().getName() + ".methods").getSubTypesOf(AbstractExecutor.class);
        for (Class<? extends AbstractExecutor> clazz : set) {
            try {
                executors.add(clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void runTaskAsync(Runnable runnable) {
//        new Thread(runnable).start();
        service.submit(runnable);
    }


    @SuppressWarnings("unchecked")
    public static <T extends AbstractExecutor<?, ?>> T getExecutor(Class<T> clazz) {
        return getInstance()
                .executors
                .stream()
                .filter(e -> e.getClass().equals(clazz))
                .map(e -> (T) e)
                .findFirst()
                .orElseThrow((() -> new IllegalArgumentException("Impossible")));
    }

    public static RequestManager getInstance() {
        return instance;
    }

    public Set<AbstractExecutor<?, ?>> getExecutor() {
        return executors;
    }

    public void shutdownPool() {
        service.shutdown();
    }

    public void resetAll() {
        for (AbstractExecutor<?, ?> executor : executors) {
            executor.reset();
        }
    }
}
