package org.wfrobotics.robot.config.robotConfigs;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

public abstract class RobotConfig
{
    //                      Intake
    // _________________________________________________________________________________
    public int INTAKE_SENSOR_R;
    public int INTAKE_SENSOR_L;
    public double INTAKE_POSSIBLE_VELOCITY_PERCENTAGE;
    public double INTAKE_P;
    public double INTAKE_I;
    public double INTAKE_D;
    public double INTAKE_F;
    public int INTAKE_VELOCITY;
    public int INTAKE_ACCELERATION;
    public int INTAKE_MAX_POSSIBLE_UP;
    public int INTAKE_TICKS_TO_TOP;
    public double INTAKE_DISTANCE_TO_BUMPER;  // centimeters
    public double INTAKE_DISTANCE_TO_CUBE;  // centimeters
    public double INTAKE_TIMEOUT_JAWS;  // seconds
    public double INTAKE_TIMEOUT_WRIST;  // seconds
    public boolean INTAKE_INVERT_RIGHT;
    public boolean INTAKE_INVERT_LEFT;

    //                      Lift
    // _________________________________________________________________________________
    public boolean LIFT_DEBUG = true;

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
    public int LIFT_TICKS_STARTING = -1500;

    //                      Tank
    // _________________________________________________________________________________
    public boolean TANK_DEBUG = true;
    public boolean TANK_SQUARE_TURN_MAG = true;

    public double TANK_MAX_VELOCITY;
    public double TANK_P;
    public double TANK_I;
    public double TANK_D;
    public double TANK_F;
    public int TANK_CRUISE_VELOCITY;
    public int TANK_ACCELERATION[];
    public int TANK_IZONE;

    public double TANK_GEAR_RATIO_ENCODER_TO_WHEEL_HIGH;  // Adjacent to encoder
    public double TANK_GEAR_RATIO_ENCODER_TO_WHEEL_LOW;  // Adjacent to encoder
    public double TANK_GEAR_RATIO_MOTOR_TO_ENCODER;  // Wheel to adjacent
    public double TANK_WHEEL_CIRCUMFERENCE;  // Adjust for thread wear as needed
    public double DISTANCE_SEPERATION_WHEEL_SIDES;  // Center to center (side to side easier to measure)

    public boolean TANK_LEFT_INVERT;
    public boolean TANK_RIGHT_INVERT;
    public boolean TANK_LEFT_SENSOR_PHASE;
    public boolean TANK_RIGHT_SENSOR_PHASE;

    public double TANK_OPEN_LOOP_RAMP;

    //                      Winch
    // _________________________________________________________________________________
    public int WINCH;
    public boolean WINCH_DOWN_IS_SAFE = false;
    public boolean WINCH_INVERT;
    public double WINCH_SPEED;
}

