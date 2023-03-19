/**
 * Contains all of the configuration for the 4360 robot
 */
package frc.robot;

import edu.wpi.first.math.util.Units;

public class Configuration {
    /********************************************
     * General
     *******************************************/
    // This controls the speed of the forward/back control. Larger numbers mean
    // faster response
    static public double forward_back_slew_rate = 0.5;
    // This controls the speed of the right to left slew rate. Large numbers mean
    // faster response
    static public double right_left_slew_rate = 0.8;
    static public double TurboFwdRevMaxLimit = 0.8;
    static public double FwdRevMaxLimit = 0.6;
    static public double TurnMaxLimit = 0.6;

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

    static public double ExpControl = 1.5;
    static public boolean EnableExpoControl = false;

    /********************************************
     * Fargo Elevator
     *******************************************/
    static public double RotationDeadband = 0.2;
    static public double RotationScale = 0.3;
    static public double ExtendDeadband = 0.2;
    static public double ElevatorKp = 0.05;
    static public double ElevatorKi = 0.0001;
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
    static public double DriveForwardTime = 5;
}
