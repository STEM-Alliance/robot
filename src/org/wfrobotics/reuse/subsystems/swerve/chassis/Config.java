package org.wfrobotics.reuse.subsystems.swerve.chassis;

import org.wfrobotics.reuse.utilities.HerdVector;

public class Config
{
    // PID ----------------------------------------------------------------------------------------
    public static final double CHASSIS_P = .025;
    public static final double CHASSIS_I = .000005;
    public static final double CHASSIS_D = 0.002;
    
    public static final double DRIVE_SPEED_CRAWL = .4;
    
    // PHYSICAL ------------------------------------------------------------------------------------
    public static final int WHEEL_COUNT = 4;
    
    public static final double WIDTH = 24.75;
    public static final double DEPTH = 28.5;
    
    public static HerdVector rBR;
    public static HerdVector rFR;
    public static HerdVector rFL;
    public static HerdVector rBL;
    
    // CONFIG --------------------------------------------------------------------------------------
    public static boolean ENABLE_ACCELERATION_LIMIT = true;
    public static boolean CRAWL_MODE_ENABLE = true;
    public static boolean CRAWL_MODE_DEFAULT_HIGH = true;
    public static boolean ENABLE_SQUARE_MAGNITUDE = true;
    public static boolean ENABLE_ROTATION_LIMIT = true;
    
    // CALIBRATION ---------------------------------------------------------------------------------
    public static double accelerationMax = 6; // Smaller is slower acceleration
    public static double crawlModeMagnitude = 0.0;  //Amount to scale back speeds (range: 0 (min - no crawl) to 1 (max - basically don't move))
    public static double rotationAdjustMin = .3;

    // CALCULATIONS --------------------------------------------------------------------------------
    static
    {
        double offsetX = WIDTH;
        double offsetY = DEPTH;

        rBR = new HerdVector(Math.sqrt(offsetX * offsetX + offsetY * offsetY), Math.atan2(offsetY, -offsetX) * 180 / Math.PI);
        rFR = new HerdVector(Math.sqrt(offsetX * offsetX + offsetY * offsetY), Math.atan2(offsetY, offsetX) * 180 / Math.PI);
        rFL = new HerdVector(Math.sqrt(offsetX * offsetX + offsetY * offsetY), Math.atan2(-offsetY, offsetX) * 180 / Math.PI);
        rBL = new HerdVector(Math.sqrt(offsetX * offsetX + offsetY * offsetY), Math.atan2(-offsetY, -offsetX) * 180 / Math.PI);
        
        double unitVectorCorrection = 1 / rBR.getMag();
        rBR = rBR.scale(unitVectorCorrection);
        rFR = rFR.scale(unitVectorCorrection);
        rFL = rFL.scale(unitVectorCorrection);
        rBL = rBL.scale(unitVectorCorrection);
    }
}
