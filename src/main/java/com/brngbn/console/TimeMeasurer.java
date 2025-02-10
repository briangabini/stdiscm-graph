package com.brngbn.console;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeMeasurer {
    private static TimeMeasurer sharedInstance;

    private long startTime;

    private TimeMeasurer() {
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
}