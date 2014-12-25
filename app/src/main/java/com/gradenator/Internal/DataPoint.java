package com.gradenator.Internal;

import com.gradenator.Utilities.Util;

/**
 * Represents a single data point for a Class for graphing purposes.
 */
public class DataPoint {

    private long timeRecorded;
    private double percentage;

    public DataPoint(double percentage, long timeRecorded) {
        this.timeRecorded = timeRecorded;
        this.percentage = percentage;
    }

    public long getTimeRecorded() {
        return timeRecorded;
    }

    public double getPercentage() {
        return percentage;
    }

    public String getDate() {
        return Util.createDate(timeRecorded);
    }

}
