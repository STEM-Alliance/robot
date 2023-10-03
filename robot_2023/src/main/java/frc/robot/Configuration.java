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
    static public double forward_back_slew_rate = 2;
    // This controls the speed of the right to left slew rate. Large numbers mean
    // faster response
    static public double right_left_slew_rate = 1.5;
    static public double encoder_counts_to_inches = 2.04;
    // This controls the XBox joystick dead zones. Any value less than this value
    // will have 0 effect
    static public double controller_dead_zone = 0.2;
    // This is the slow mode. It rerates the max speed of the forward/back and
    // left/right to 0.6
    static public double fine_controller_derate = 0.5;
    static public double DriveMaxFwdSpeed = .5;
    static public double DriveMaxROTSpeed = .6;
    /********************************************
     * Gripper Controlers
     *******************************************/
    static public boolean RotateGripper = false;
    // make SURE to set the gripper motor type to brushed
     static public double GripperCloseSetPoint = 0.5;
    // Any value between -0.2 and 0.2 will NOT move the gripper
    static public double GripperDeadband = 0.2;
    static public double GripperMaxSpeed = 0.4;
    /********************************************
     * Elevator Controlers
     *******************************************/
    static public double ExtendMaxSpeed = 0.2;




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
    static public boolean Simulate = true;
}
