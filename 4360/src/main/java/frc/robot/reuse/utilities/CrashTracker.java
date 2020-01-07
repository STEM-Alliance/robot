package frc.robot.reuse.utilities;

import java.io.PrintWriter;
import java.util.Date;
import java.util.UUID;

/**
 * Tracks start-up and caught crash events
 */
public class CrashTracker {

    private static final UUID RUN_INSTANCE_UUID = UUID.randomUUID();

    public static void logRobotStartup() {
        logMarker("robot startup");
    }

    public static void logRobotConstruction() {
        logMarker("robot startup");
    }

    public static void logRobotInit() {
        logMarker("robot init");
    }

    public static void logTeleopInit() {
        logMarker("teleop init");
    }

    public static void logAutoInit() {
        logMarker("auto init");
    }

    public static void logDisabledInit() {
        logMarker("disabled init");
    }

    public static void logThrowableCrash(Throwable throwable) {
        logMarker("Exception", throwable);
    }

    private static void logMarker(String mark) {
        logMarker(mark, null);
    }

    private static void logMarker(String mark, Throwable nullableException) {

        try (PrintWriter writer = new PrintWriter(System.out)) {
            writer.print(RUN_INSTANCE_UUID.toString());
            writer.print(", ");
            writer.print(mark);
            writer.print(", ");
            writer.print(new Date().toString());

            if (nullableException != null) {
                writer.print(", ");
                nullableException.printStackTrace(writer);
            }

            writer.println();
        }
    }
}
