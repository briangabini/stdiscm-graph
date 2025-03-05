package com.brngbn.console;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class TimeMeasurer {
    private static volatile TimeMeasurer instance; // Thread-safe singleton
    private final List<Long> durations; // Stores all measured durations
    private final ConcurrentMap<String, Long> timers; // Support multiple timers

    private TimeMeasurer() {
        durations = new ArrayList<>();
        timers = new ConcurrentHashMap<>();
    }

    /**
     * Returns the singleton instance using double-checked locking for thread safety.
     */
    public static TimeMeasurer getInstance() {
        if (instance == null) {
            synchronized (TimeMeasurer.class) {
                if (instance == null) {
                    instance = new TimeMeasurer();
                }
            }
        }
        return instance;
    }

    /**
     * Resets the singleton instance (for testing purposes).
     */
    public static void destroyInstance() {
        instance = null;
    }

    /**
     * Starts tracking time under a given timer name.
     */
    public void startTracking(String timerName) {
        timers.put(timerName, System.currentTimeMillis());
    }

    /**
     * Calculates the duration of a named timer.
     */
    public long calculateDuration(String timerName) {
        Long startTime = timers.get(timerName);
        if (startTime == null) {
            log.warn("Timer '{}' not found.", timerName);
            return -1;
        }
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Calculates and prints the duration for a named timer.
     */
    public void calculateAndPrintDuration(String timerName) {
        long duration = calculateDuration(timerName);
        if (duration >= 0) {
            System.out.println("Time taken for " + timerName + ": " + duration + " ms");
        }
    }

    /**
     * Adds a recorded duration to the list.
     */
    public void addDuration(long duration) {
        durations.add(duration);
    }

    /**
     * Returns the average duration using LongSummaryStatistics.
     */
    public double getAverageDuration() {
        if (durations.isEmpty()) {
            return 0;
        }
        return durations.stream().mapToLong(Long::longValue).average().orElse(0);
    }

    /**
     * Returns detailed statistics for all recorded durations.
     */
    public LongSummaryStatistics getDurationStatistics() {
        return durations.stream().mapToLong(Long::longValue).summaryStatistics();
    }

    /**
     * Clears all stored durations.
     */
    public void clearDurations() {
        durations.clear();
    }
}
