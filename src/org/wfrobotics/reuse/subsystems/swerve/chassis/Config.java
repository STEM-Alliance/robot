package org.wfrobotics.reuse.subsystems.swerve.chassis;

import org.wfrobotics.reuse.utilities.HerdVector;

// Move some into robot specific drive config?

public class Config
{
    // PID ----------------------------------------------------------------------------------------
    public static final double CHASSIS_P = .025;
    public static final double CHASSIS_I = .000005;
    public static final double CHASSIS_D = 0.002;

    public static final double DRIVE_SPEED_CRAWL = .4;

    // PHYSICAL ------------------------------------------------------------------------------------
    public static final double WIDTH = 24.75;
    public static final double DEPTH = 28.5;

    public static HerdVector rBR;
    public static HerdVector rFR;
    public static HerdVector rFL;
    public static HerdVector rBL;

    public static final int[] SHIFTER_VALS = { 100, 80, 90, 120};
    public static final int SHIFTER_RANGE = 55;

    // CONFIG --------------------------------------------------------------------------------------
    public static boolean ENABLE_ACCELERATION_LIMIT = true;
    public static boolean CRAWL_MODE_ENABLE = true;
    public static boolean CRAWL_MODE_DEFAULT_HIGH = true;
    public static boolean ENABLE_SQUARE_MAGNITUDE = true;
    public static boolean ENABLE_ROTATION_LIMIT = true;
    public static final boolean[] SHIFTER_INVERT = {true, false, true, false};

    // CALIBRATION ---------------------------------------------------------------------------------
    public static double accelerationMax = 6; // Smaller is slower acceleration
    public static double crawlModeMagnitude = 0.0;  //Amount to scale back speeds (range: 0 (min - no crawl) to 1 (max - basically don't move))
    public static double rotationAdjustMin = .3;
    public static final double SHIFTER_SHIFT_TIME = 1;  // Seconds for servo to shift, // TODO DRL calibrate/test

    // CALCULATIONS --------------------------------------------------------------------------------
    static
    {
        HerdVector rWidth = new HerdVector(WIDTH, 90);
        HerdVector rHeight = new HerdVector(DEPTH, 0);

        rFR = rWidth.add(rHeight);
        rBR = rWidth.sub(rHeight);
        rFL = rWidth.rotate(180).add(rHeight);
        rBL = rWidth.rotate(180).sub(rHeight);

        double unitVectorCorrection = 1 / rBR.getMag();
        rFR = rFR.scale(unitVectorCorrection);
        rFL = rFL.scale(unitVectorCorrection);
        rBR = rBR.scale(unitVectorCorrection);
        rBL = rBL.scale(unitVectorCorrection);
    }
}
