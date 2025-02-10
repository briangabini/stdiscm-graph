package com.brngbn.console;

import lombok.extern.slf4j.Slf4j;
import org.jfree.util.Log;

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

    public void calculateStartTime() {
        startTime = System.nanoTime();
    }

    public void calculateEndTimeAndDuration() {
        long endTime = System.nanoTime();
//        Log.debug("Time taken: " + (endTime - startTime) + " ms");
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
    }
}