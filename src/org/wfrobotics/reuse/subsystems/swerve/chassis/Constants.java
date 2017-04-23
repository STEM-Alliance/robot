package org.wfrobotics.reuse.subsystems.swerve.chassis;

public class Constants
{
    public static final int WHEEL_COUNT = 4;
    
    public static final double CHASSIS_P = .025;
    public static final double CHASSIS_I = .000005;
    public static final double CHASSIS_D = 0.002;
    
    public static final double DRIVE_SPEED_CRAWL = .4;
    
    public static final double WIDTH = 24.75;
    public static final double DEPTH = 28.5;
    public static final double SCALE = DEPTH;// TODO Use a magnitude of 1: Math.sqrt(CHASSIS_WIDTH * CHASSIS_WIDTH + CHASSIS_DEPTH * CHASSIS_DEPTH);
    
    public static final String WHEEL_IDS[] = {"WheelFL", "WheelFR", "WheelBR", "WheelBL"};
    public static final double[][] WHEEL_POSITIONS = {
            {-WIDTH / SCALE, DEPTH / SCALE}, // Front left
            {WIDTH / SCALE, DEPTH / SCALE}, // Front right
            {WIDTH / SCALE, -DEPTH / SCALE}, // Back right
            {-WIDTH / SCALE, -DEPTH / SCALE}}; // Back left
}
