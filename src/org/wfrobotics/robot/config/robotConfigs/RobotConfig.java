package org.wfrobotics.robot.config.robotConfigs;

import org.wfrobotics.reuse.config.RobotConfigPicker;
import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TankConfig.TankConfigSupplier;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

public abstract class RobotConfig implements TankConfigSupplier
{
    private static RobotConfig instance = null;

    public final int CAN_PNEUMATIC_CONTROL_MODULE = 7;

    //                      Intake
    // _________________________________________________________________________________
    public final int CAN_INTAKE_LEFT = 19;
    public final int CAN_INTAKE_RIGHT = 20;
    public final int PNEUMATIC_INTAKE_HORIZONTAL_FORWARD = 2;
    public final int PNEUMATIC_INTAKE_HORIZONTAL_REVERSE = 3;

    public int INTAKE_SENSOR_R;

    public double INTAKE_DISTANCE_TO_CUBE;  // centimeters
    public double INTAKE_TIMEOUT_JAWS;  // seconds
    public double INTAKE_TIMEOUT_WRIST;  // seconds
    public boolean INTAKE_INVERT_RIGHT;
    public boolean INTAKE_INVERT_LEFT;

    //                      Lift
    // _________________________________________________________________________________
    public boolean LIFT_DEBUG = false;

    public ClosedLoopConfig LIFT_CLOSED_LOOP;
    public LimitSwitchNormal[] LIFT_LIMIT_SWITCH_NORMALLY;
    public int LIFT_TICKS_STARTING = -1500;

    //                      Winch
    // _________________________________________________________________________________
    public int WINCH;
    public boolean WINCH_INVERT;
    public double WINCH_SPEED;

    //                      Wrist
    // _________________________________________________________________________________
    public boolean WRIST_TUNING = false;
    public ClosedLoopConfig WRIST_CLOSED_LOOP;
    public double WRIST_DEADBAND;
    public int WRIST_TICKS_TO_TOP;
    public final int INTAKE_LIFT_FORWARD_LIMIT = 4600;
    public final int INTAKE_LIFT_REVERSE_LIMIT= 0;

    public static RobotConfig getInstance()
    {
        if (instance == null)
        {
            instance = RobotConfigPicker.get(new RobotConfig[] { new HerdVictor() });
        }
        return instance;
    }
}

