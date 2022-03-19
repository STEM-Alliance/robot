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
    static Team robot = Team.FARGO;
    // This controls the speed of the forward/back control. Larger numbers mean faster response
    static double forward_back_slew_rate = 3;
    // This controls the speed of the right to left slew rate. Large numbers mean faster response
    static double right_left_slew_rate = 2;
    // TODO: This controls the encoder counts to inches. This is really a guess and needs to be fixed
    static double encoder_counts_to_inches = 1;
    // This controls the XBox joystick dead zones. Any value less than this value will have 0 effect
    static double controller_dead_zone = 0.2;
    // This is the slow mode. It rerates the max speed of the forward/back and left/right to 0.6
    static double fine_controller_derate = 0.6;
    // This is the maximum speed, in percent, of the harvester motor
    static double harvester_max_speed = 0.45;
    // This is the maximum speed, in percent, of the indexer
    static double indexer_max_speed = 0.25;
    // This is the maximum speed, in percent, of the climbing arms
    static double climber_max_speed = 0.75;
}

