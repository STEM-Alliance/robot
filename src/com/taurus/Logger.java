package com.taurus;

import java.io.PrintStream;

/**
 * This class is for logging of information. Initially it will only be plain
 * text data to the console but it can be further implemented later to send
 * actual quantitative data to the dashboard.
 *
 * @author Tanner Danzey
 */
public class Logger {

    //------------------
    // Private Variables
    //------------------
    private final PrintStream output;

    private String tag = "[4818.14]";
    private final String warnTag = "[-!-]";
    private final String errTag = "[!!!]";
    private final String infoTag = "[---]";
    private final String calloutTag = ">>>>>";

    /**
     * Instantiate the logger.
     *
     * @param newTag The desired log tag for more information.
     * @param out The PrintStream object to write logs to.
     */
    public Logger(String newTag, PrintStream out) {
        tag = newTag;
        output = out;
        dbg("Logging subsystem initialized.");
    }

    public void dbg(String message) {
        output.println(tag + " " + message + "\n");
    }

    public void info(String message) {
        output.println(tag + infoTag + " " + message + "\n");
    }

    public void warn(String message) {
        output.println(tag + warnTag + " " + message + "\n");
    }

    public void error(String message) {
        output.println(tag + errTag + " " + message + "\n");
    }

}
