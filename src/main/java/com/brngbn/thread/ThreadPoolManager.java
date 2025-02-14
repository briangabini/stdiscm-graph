package com.brngbn.thread;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Getter
public class ThreadPoolManager {
    private static final int MAX_THREADS = 666666;
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

    /**
     * Execute tasks and return true if any task returns true.
     * If all tasks return false, return false instead of failing.
     */
    public boolean executeTasks(List<Callable<Boolean>> tasks) {
        ExecutorCompletionService<Boolean> completionService = new ExecutorCompletionService<>(executorService);
        List<Future<Boolean>> futures = new ArrayList<>();

        // Submit all tasks
        for (Callable<Boolean> task : tasks) {
            futures.add(completionService.submit(task));
        }

        try {
            for (int i = 0; i < tasks.size(); i++) {
                Future<Boolean> resultFuture = completionService.take();  // Async wait
                Boolean result = resultFuture.get();

                if (result) {
                    // Cancel all remaining tasks
                    for (Future<Boolean> future : futures) {
                        future.cancel(true);
                    }
                    return true;  // If true, return immediately
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Task execution error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        return false;  // Only return false if no task found `true`
    }

    public void shutdown() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
