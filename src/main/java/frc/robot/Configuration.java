/**
 * Contains all of the configuration for the 4360 robot
 */
package frc.robot;

import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;

public class Configuration {
    /********************************************
     * Swerve and Controls
     *******************************************/
    // This controls the speed of the right to left slew rate. Large numbers mean
    // faster response
    static public double kVxSlewRateLimit = 8;
    static public double kVySlewRateLimit = 8;
    static public double kOmegaSlewRateLimit = 10;
    static public double kDrivingOmegaFactor = 0.3;

    // Max speeds
    static public double kMaxSpeed = 6; // 3 meters per second
    static public double kMaxAngularSpeed = 2 * Math.PI; // 2 rotations per second 
    static public double kMaxAngularAcceleration = Math.pow(2 * Math.PI, 2);
    static public double GeneralDeadband = 0.2;

    // PID and Feedforward gains for the drive motors
    // NOTE: We are running the drive motors in open loop mode, using only the feedfoward.
    static public double kDriveKp = 0;
    static public double kDriveKi = 0;
    static public double kDriveKd = 0;
    static public double kDriveKs = 0.05;
    static public double kDriveKv = 0.125;

    // PID and feedforward gains for the swerve motors
    static public double kSwerveKp = 0.5; // 0.65
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
        new Translation2d(0.2286, 0.22225),
        new Translation2d(0.2286, -0.22225),
        new Translation2d(-0.2286, 0.22225),
        new Translation2d(-0.2286, -0.22225)
    };

    // Module Index, Drive Motor Channel, Swerve Motor Channel, Analog Encoder Channel
    static public int[] kSwerveFLCanID = new int[]{0, 1, 2, 0};
    static public int[] kSwerveFRCanID = new int[]{1, 3, 4, 1};
    static public int[] kSwerveBLCanID = new int[]{2, 5, 6, 2};
    static public int[] kSwerveBRCanID = new int[]{3, 7, 8, 3};

    static public int kPigeon2CanID = 40;

    // NOTE: These seem to drift
    //static public double[] kZeroPosition = new double[]{0.963, 0.486, 0.070, 0.093};
    static public double[] kZeroPosition = new double[]{0.963, 0.486, 0.710, 0.093};
    static public double kEncoderRes = 4096;

    static public HolonomicPathFollowerConfig kPathFollowerConfig =
        new HolonomicPathFollowerConfig(
            new PIDConstants(0.85), // Translation
            new PIDConstants(0.65), // Rotation
            kMaxSpeed,
            kSwerveTranslations[0].getNorm(),
            new ReplanningConfig()
        );
    
    static public int KRumbleTimer = 100;

    /********************************************
     * Shooter Configuration
     *******************************************/
    static public int kShooterArmMotorCanID = 14;
    static public int kShooterArmEncoderChannel = 1;

    static public double kShooterArmKp = 0.1;
    static public double kShooterArmKi = 0;
    static public double kShooterArmKd = 0;
    static public double kShooterArmMaxSpeed = 60; // Degrees/s
    static public double kShooterArmAcceleration = kShooterArmMaxSpeed / 0.5; // Max in 0.5s
    static public double kShooterArmRatio = 360/230 * 1.5;
    static public double kShooterArmChangeRate = 50; // Increase desired angle x degrees per second
    static public double kShooterArmPIDLimit = 0.3; // Limit the PID output incase of wrong values

    static public double kTargetingError = 1; // +- Error to be at setpoint
    static public double kShooterArmLowerLimit = 0;
    static public double kShooterArmUpperLimit = 0;

    // Intake Position, Travel Position, Amp Position
    static public double kShooterArmUnhookPosition = 5;
    static public double kShooterArmLoweredPosition = -60;
    static public double[] kShooterArmSetpoints = new double[]{-50, 0, 40};

    static public double kMinFlywheelSpeed = 90;

    static public int[] kShooterMotorCanID = new int[]{11, 12};
    static public int kNoteSensorChannel = 0;

    /********************************************
     * Intake Configuration
     *******************************************/
    static public int kIntakeMotorCanID = 10;

    /********************************************
     * Limelight Configuration
     *******************************************/
    static public String kLimelightName = "limelight";

    /********************************************
     * Climber Configuration
     *******************************************/
    static public double kClimberAligningSpeed = 1;
    static public int kStageSensorChannel = 2;
    static public int kClimbMotorCanID = 13;
    static public int kClimbStopChannel = 0;
    
    /********************************************
     * Motor Current Limits
     * P = VI
     * I = P / V
     *******************************************/
    static public int NeoLimit = 80;
    static public int Neo550Limit = 30;
    static public int BagMotorLimit = 30; // Max power is 149 W, 12.4 A
    static public int M775ProLimit = 15; // Max power 347 W, 28.9 A
    static public int CIMSLimit = 28; // Max power 337 W, 28.0 A
    // https://firstwiki.github.io/wiki/denso-window-motor
    static public int WindowLimit = 15; // This seems safe

    static public double kExpControl = 1.5;
    static public boolean EnableExpoControl = false;

    /********************************************
     * Aimbot
     *******************************************/
    static public double kAimP = 0.000025; // 0.005
    static public double kAimI = 0;
    static public double kAimD = 0;
    static public double kAimSpeedLimit = 0.15;
    static public double kAimbotStop = 1; // 8

    /********************************************
     * TrapAim
     *******************************************/
    static public double kTrapAimPs = 0.04;
    static public double kTrapAimIs = 0.04;
    static public double kTrapAimDs = 0;

    static public double kTrapAimPr = 0.04;
    static public double kTrapAimIr = 0.04;
    static public double kTrapAimDr = 0;

    static public double kTrapAimSpeedLimit = 0.5;
    static public double kTrapAimbotStop = 1;

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
