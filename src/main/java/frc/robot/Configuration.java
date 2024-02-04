/**
 * Contains all of the configuration for the 4360 robot
 */
package frc.robot;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;

public class Configuration {
    /********************************************
     * Swerve and Controls
     *******************************************/
    // This controls the speed of the right to left slew rate. Large numbers mean
    // faster response
    static public double kVxSlewRateLimit = 10;
    static public double kVySlewRateLimit = 10;
    static public double kOmegaSlewRateLimit = 10;
    // Max speeds
    static public double kMaxSpeed = 0.5; // 3 meters per second
    static public double kMaxAngularSpeed = 4 * Math.PI; // 1 rotation per second 
    static public double GeneralDeadband = 0.2;

    // PID Values
    static public double kDriveKp = 0;
    static public double kDriveKi = 0;
    static public double kDriveKd = 0;
    static public double kDriveKs = 0.1;
    static public double kDriveKv = 0.25;

    static public double kSwerveKp = 0.65; // 0.65
    static public double kSwerveKi = 0; // 0.1
    static public double kSwerveKd = 0; // 0
    static public double kSwerveKs = 0;
    static public double kSwerveKv = 0;

    // Swerve Hardware
    static public double kWheelRadius = 0.0508;
    static public double kDriveGearReduction = 6.12;
    static public double kTurningGearReduction = 12.8;
    
    // FL, FR, BL, BR
    static public Translation2d[] kSwerveTranslations = new Translation2d[]{
        new Translation2d(0.3303, 0.3334),
        new Translation2d(0.3303, -0.3334),
        new Translation2d(-0.3303, 0.3334),
        new Translation2d(-0.3303, -0.3334)
    };

    // Module Index, Drive Motor Channel, Swerve Motor Channel, Analog Encoder Channel
    static public int[] kSwerveFLChannels = new int[]{0, 4, 3, 0};
    static public int[] kSwerveFRChannels = new int[]{1, 1, 2, 1};
    static public int[] kSwerveBLChannels = new int[]{2, 8, 7, 2};
    static public int[] kSwerveBRChannels = new int[]{3, 5, 6, 3};

    static public double[] kZeroPosition = new double[]{290, 2800, 2290, 2381};
    public static double kEncoderRes = 4096;

    /********************************************
     * Motor Current Limits
     * P = VI
     * I = P / V
     *******************************************/
    static public int NeoLimit = 80;
    static public int Neo550Limit = 20;
    static public int BagMotorLimit = 30; // Max power is 149 W, 12.4 A
    static public int M775ProLimit = 15; // Max power 347 W, 28.9 A
    static public int CIMSLimit = 28; // Max power 337 W, 28.0 A
    // https://firstwiki.github.io/wiki/denso-window-motor
    static public int WindowLimit = 15; // This seems safe

    static public double kExpControl = 1.5;
    static public boolean EnableExpoControl = false;

    /********************************************
     * Fargo Elevator
     *******************************************/
    static public double RotationDeadband = 0.2;
    static public double RotationScale = 0.3;
    static public double ExtendDeadband = 0.2;
    static public double ElevatorKp = 0.04;
    static public double ElevatorKi = 0.0005;
    static public double ElevatorKd = 0;
    static public double ElevatorScale = 0.1;

    /********************************************
     * Gripper Controlers
     *******************************************/
    // Any value between -0.2 and 0.2 will NOT move the gripper
    static public double GripperDeadband = 0.2;
    static public double GripperOpenCloseSpeed = 0.3;
    static public double GripperSlideFast = 0.3;
    static public double GripperSlideSlow = 0.2;
    static public double RotateMotorMaxSpeed = 0.4;

    /********************************************
     * Autonomous Control
     *******************************************/
    /*
     * Per the spec the Neo motors have 42 pulses/rotation
     * The Neo encoder reports in rotations
     * The neo rotates ~8.5 times per one wheel rotation
     * The wheels are 6 inches
     *
     * The velocity is reported in RPMs, it appears the max is around 5800 RPM or
     * so.
     *
     * circumference = 2 * pi * r
     *
     * 1 rotation of the main drive wheel is 2 * pi * 3 inches = 18.85 inches or
     * 0.479 meters
     * Or 1 motor rotation is 0.479 meters/9 = 0.053 meters
     * At max speed the robot can go 0.053 meters * 5800 RPM / 60 = 5.12 meters/sec
     *
     * So if we are given meters/second for the wheels, we need to convert that into
     */
    static public double kRamseteB = 2;
    static public double kRamsetsZeta = 0.7;
    static public double kMaxSpeedMetersPerSecond = 2;
    static public double kMaxAccelerationMetersPerSecondSquared = 0.3;
    static public double TrackWidthInMeters = Units.inchesToMeters(26.5);
    static public double MetersPerRotation = 0.0508;
    static public boolean Simulate = false;
    static public double AutoArmPosition = 38;
    static public double ExtendArmPosition = 1;
    static public double MaxAutoSpeed = 0.6;
    static public double MaxLevelSpeed = 0.4;
}
