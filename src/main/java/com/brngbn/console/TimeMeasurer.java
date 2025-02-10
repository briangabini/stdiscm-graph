package com.brngbn.console;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TimeMeasurer {
    private static TimeMeasurer sharedInstance;

    private long startTime;
    private List<Long> durations;

    private TimeMeasurer() {
        durations = new ArrayList<>();
    }

    public static synchronized TimeMeasurer getInstance() {
        if (sharedInstance == null) {
            sharedInstance = new TimeMeasurer();
        }
        return sharedInstance;
    }

    public static synchronized void destroyInstance() {
        sharedInstance = null;
    }

    public void startTracking() {
        startTime = System.nanoTime();
    }

    public long calculateDuration() {
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public void calculateAndPrintDuration() {
        long duration = calculateDuration();
        System.out.println("Time taken: " + duration + " ns");
    }

    public void addDuration(long duration) {
        durations.add(duration);
    }

    public double getAverageDuration() {
        if (durations.isEmpty()) {
            return 0;
        }
        long total = 0;
        for (long duration : durations) {
            total += duration;
        }
        return (double) total / durations.size();
    }

    public void clearDurations() {
        durations.clear();
    }
}