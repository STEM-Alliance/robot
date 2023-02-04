/**
 * Contains all of the configuration for the 4360 robot
 */
package frc.robot;

public class Configuration {
    static enum Team {
        FARGO,
        MOORHEAD,
    };

    // This variable defines the team
    static Team robot = Team.MOORHEAD;
    // This controls the speed of the forward/back control. Larger numbers mean faster response
    static double forward_back_slew_rate = 3;
    // This controls the speed of the right to left slew rate. Large numbers mean faster response
    static double right_left_slew_rate = 2;
    // TODO: This controls the encoder counts to inches. This is really a guess and needs to be fixed
    static double encoder_counts_to_inches = 2.04;
    // This controls the XBox joystick dead zones. Any value less than this value will have 0 effect
    static double controller_dead_zone = 0.2;
    // This is the slow mode. It rerates the max speed of the forward/back and left/right to 0.6
    static double fine_controller_derate = 0.5;
    // This is the maximum speed, in percent, of the harvester motor
    static double harvester_max_speed = 0.45;
    // This is the maximum speed, in percent, of the indexer
    static double indexer_max_speed = 0.75;
    // This is the maximum speed, in percent, of the climbing arms
    static double climber_max_speed = 1.0;
    // This slows down the left/right robot movement
    static double right_left_derate_percentage = 0.75;
    // Launcher high speed
    static double launcher_high_speed_percentage = 0.8;
    // Launcher medium speed
    static double launcher_medium_speed_percentage = 0.6;
    // Launcher low speed
    static double launcher_low_speed_percentage = 0.4;

    // AUTONOMOUS MODE VARIABLES
    // Delay to allow the launcher to spin up
    static double launcher_spin_up_delay = 1;
    // Delay to drive the indexer to shoot
    static double indexer_launch_delay = 3;
    // Auto mode drive speed
    static double auto_mode_drive_speed = 0.4;
    // Auto mode exist tarmac speec
    static double auto_exit_tarmac_speed = 0.75;
    // How far to move the robot
    static double auto_move_distance = 48;
    // How far to move the robot out of the tarmac
    static double auto_move_out_of_tarmac = 55;

    // RAMSETE controoller
    static double kRamseteB = 2;
    static double kRamsetsZeta = 0.5;
    static double kMaxSpeedMetersPerSecond = 4;
    static double kMaxAccelerationMetersPerSecondSquared = 0.5;
    static double TrackWidth = 26.5;
    static double MetersPerRotation = 0.053;
    static boolean Simulate = false;
    static double MaxRobotSpeedMPS = 5800 / 60 * MetersPerRotation;
}

