package com.brngbn.thread;

import lombok.Getter;

import java.util.List;
import java.util.concurrent.*;

@Getter
public class ThreadPoolManager {
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    private static ThreadPoolManager instance;
    private final ExecutorService executorService;

    private ThreadPoolManager() {
        executorService = Executors.newFixedThreadPool(MAX_THREADS);
    }

    public static synchronized ThreadPoolManager getInstance() {
        if (instance == null) {
            instance = new ThreadPoolManager();
        }
        return instance;
    }

    public <T> boolean executeTasks(List<Callable<Boolean>> tasks) {
        try {
            List<Future<Boolean>> futures = executorService.invokeAll(tasks);
            for (Future<Boolean> future : futures) {
                if (future.get()) {
                    return true;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Error executing tasks: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted state
        }
        return false;
    }

    public void shutdown() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
