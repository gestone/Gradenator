package com.gradenator.Internal;

/**
 * Constant class used to improve readability throughout the rest of the application.
 */
public class Constant {

    /* Restricts access */
    private Constant() {

    }

    public static final double NO_ASSIGNMENTS = -1.0;

    public static final int OVERVIEW_FRAGMENT = 0;

    public static final int ALL_ASSIGNMENTS_FRAGMENT = 1;

    public static final int GRAPH_FRAGMENT = 2;

    public static final int CALCULATOR_FRAGMENT = 3;

    public static final long ONE_MINUTE = 60000; // for testing purposes for the graph in millis

    public static final long ONE_DAY = 86400000;

    public static final long ONE_WEEK = 604800000;

    public static final String DEFAULT_FONT = "quicksandregular.ttf";

    public static final String ALARM_ON = "Alarm_On";

    public static final String[] DEFAULT_COLOR_SCHEME = new String[]{"#78B400", "#588303",
            "#93AD1E", "#78B400", "#B4961F", "#3E3308", "#354B00", "#3E3608", "#3E1908",
            "#312A3F", "#29CDFF", "#163607", "#0B2100", "#8C5603", "#F0BD1B"};

}
