package org.wfrobotics.robot.config.robotConfigs;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

public abstract class RobotConfig
{
    //                      Intake
    // _________________________________________________________________________________
    public int INTAKE_SENSOR;

    public double INTAKE_DISTANCE_TO_CUBE;  // centimeters
    public double INTAKE_TIMEOUT_JAWS;  // seconds
    public double INTAKE_TIMEOUT_WRIST;  // seconds
    public boolean INTAKE_INVERT_RIGHT;
    public boolean INTAKE_INVERT_LEFT;

    //                      Lift
    // _________________________________________________________________________________
    public double LIFT_SPROCKET_DIAMETER_INCHES; // 1.29 16 tooth 25 chain
    protected double LIFT_MAX_POSSIBLE_UP; // bottom sensor to top sensor
    protected double LIFT_POSIBLE_VELOCITY_PERCENTAGE; // percentage
    public double LIFT_P;
    public double LIFT_I;
    public double LIFT_D;
    public double LIFT_F;
    public int LIFT_VELOCITY;
    public int LIFT_ACCELERATION;
    public boolean LIFT_MOTOR_INVERTED_LEFT;
    public boolean LIFT_MOTOR_INVERTED_RIGHT;

    public boolean LIFT_SENSOR_PHASE_LEFT;
    public boolean LIFT_SENSOR_PHASE_RIGHT;
    public LimitSwitchNormal[][] LIFT_LIMIT_SWITCH_NORMALLY;

    //                      Tank
    // _________________________________________________________________________________
    public boolean TANK_SQUARE_TURN_MAG = true;

    public double TANK_MAX_VELOCITY;
    public double TANK_P;
    public double TANK_I;
    public double TANK_D;
    public double TANK_F;
    public int TANK_CRUISE_VELOCITY;
    public int TANK_ACCELERATION;
    public int TANK_IZONE;

    public boolean TANK_LEFT_INVERT;
    public boolean TANK_RIGHT_INVERT;
    public boolean TANK_LEFT_SENSOR_PHASE;
    public boolean TANK_RIGHT_SENSOR_PHASE;

    public double TANK_OPEN_LOOP_RAMP;

    public boolean TANK_SWAP_LEFT_RIGHT;

    //                      Winch
    // _________________________________________________________________________________
    public int WINCH;
    public boolean WINCH_INVERT;
    public double WINCH_SPEED;
}

