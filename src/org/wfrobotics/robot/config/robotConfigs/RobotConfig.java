package org.wfrobotics.robot.config.robotConfigs;

public abstract class RobotConfig
{
    //                      Intake
    // _________________________________________________________________________________
    public double INTAKE_DISTANCE_TO_CUBE; // centimeters
    public double INTAKE_WRIST_TIMEOUT_LENTH; // secounds

    //                      Lift
    // _________________________________________________________________________________
    public double LIFT_SPROCKET_DIAMETER_INCHES; // 1.29 16 tooth 25 chain
    public double LIFT_MAX_POSSIBLE_UP; // bottom sensor to top sensor
    public double LIFT_P;
    public double LIFT_I;
    public double LIFT_D;
    public double LIFT_F;
    public double LIFT_POSIBLE_VELOCITY_PERCENTAGE; // percentage
    public boolean LIFT_MOTOR_INVERTED_LEFT;
    public boolean LIFT_MOTOR_INVERTED_RIGHT;

    public boolean LIFT_SENSOR_PHASE_LEFT;
    public boolean LIFT_SENSOR_PHASE_RIGHT;

    //                      Tank
    // _________________________________________________________________________________

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

