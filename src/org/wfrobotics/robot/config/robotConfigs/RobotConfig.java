package org.wfrobotics.robot.config.robotConfigs;

import org.wfrobotics.reuse.hardware.Gains;
import org.wfrobotics.reuse.subsystems.drive.TankConfig.TankConfigSupplier;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

public abstract class RobotConfig implements TankConfigSupplier
{
    //                      Intake
    // _________________________________________________________________________________
    public int INTAKE_SENSOR_R;

    public double INTAKE_DISTANCE_TO_BUMPER;  // centimeters
    public double INTAKE_DISTANCE_TO_CUBE;  // centimeters
    public double INTAKE_TIMEOUT_JAWS;  // seconds
    public double INTAKE_TIMEOUT_WRIST;  // seconds
    public boolean INTAKE_INVERT_RIGHT;
    public boolean INTAKE_INVERT_LEFT;

    //                      Lift
    // _________________________________________________________________________________
    public boolean LIFT_DEBUG = false;

    public double LIFT_SPROCKET_DIAMETER_INCHES;
    protected double LIFT_MAX_POSSIBLE_UP;
    protected double LIFT_MAX_POSSIBLE_DOWN;
    protected double LIFT_MAX_POSSIBLE_VELOCITY;
    protected double LIFT_POSIBLE_VELOCITY_PERCENTAGE; // percentage
    public Gains[] LIFT_GAINS;
    public int LIFT_VELOCITY[];
    public int LIFT_ACCELERATION[];

    public boolean LIFT_MOTOR_INVERTED_LEFT;
    public boolean LIFT_MOTOR_INVERTED_RIGHT;
    public boolean LIFT_SENSOR_PHASE_LEFT;
    public boolean LIFT_SENSOR_PHASE_RIGHT;
    public LimitSwitchNormal[][] LIFT_LIMIT_SWITCH_NORMALLY;
    public int LIFT_TICKS_STARTING = -1500;

    //                      Winch
    // _________________________________________________________________________________
    public int WINCH;
    public boolean WINCH_INVERT;
    public double WINCH_SPEED;

    //                      Wrist
    // _________________________________________________________________________________
    public double WRIST_POSSIBLE_VELOCITY_PERCENTAGE;
    public int WRIST_MAX_POSSIBLE_UP;
    public int WRIST_TICKS_TO_TOP;
    public Gains WRIST_GAINS;
    public int WRIST_VELOCITY;
    public int WRIST_ACCELERATION;
}

