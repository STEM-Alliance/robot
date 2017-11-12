package org.wfrobotics.reuse.subsystems.swerve;

import org.wfrobotics.reuse.utilities.HerdVector;

// TODO Move some into robot specific drive config?

/**
 * @author Team 4818 WFRobotics
 */
public class Config
{
    private static final double WIDTH = 24.75;
    private static final double DEPTH = 28.5;

    // CHASSIS ----------------------------------------------------------------------------------------

    public static final double CHASSIS_P = .025;
    public static final double CHASSIS_I = .000005;
    public static final double CHASSIS_D = 0.002;

    public static double accelerationMax = 6; // Smaller is slower acceleration
    /** Allows favoring chassis velocity over chassis spinning. The faster the chassis spins, the slower it is simultaneously moving across the field */
    public static double rotationAdjustMin = .3;

    // WHEELS ------------------------------------------------------------------------------------

    public static final double WHEEL_ANGLE_P = .018;
    public static final double WHEEL_ANGLE_I = 0.0000075;
    public static final double WHEEL_ANGLE_D = 0.0001;

    public static HerdVector rBR;
    public static HerdVector rFR;
    public static HerdVector rFL;
    public static HerdVector rBL;

    /** Wheel rotation motors are limited to this percentage of max possible motor speed */
    public static double WHEEL_ANGLE_SPEED_MAX = -.7;

    // SHIFTERS --------------------------------------------------------------------------------------

    public static final int[] SHIFTER_VALS = { 100, 80, 90, 120};
    public static final int SHIFTER_RANGE = 55;
    public static final double SHIFTER_SHIFT_TIME = 1;  // Seconds for servo to shift, // TODO DRL calibrate/test
    public static final boolean[] SHIFTER_INVERT = {true, false, true, false};

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
