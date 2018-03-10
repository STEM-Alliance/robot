package org.wfrobotics.robot;

import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LimitSwitch
{
    private enum Limit
    {
        BOTTOM,
        TOP
    }
    RobotConfig config;
    private final boolean[][] invertSensorReading = new boolean[2][2];
    private TalonSRX[] motors;
    public LimitSwitch(TalonSRX[] motors, RobotConfig config){
        this.motors = motors;
        final int kTimeout = 10;

        for(int i = 0; i < this.motors.length; i++){
            motors[i].configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, config.LIFT_LIMIT_SWITCH_NORMALLY[i][0], kTimeout);
            motors[i].configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, config.LIFT_LIMIT_SWITCH_NORMALLY[i][1], kTimeout);
            motors[i].overrideLimitSwitchesEnable(true);
            invertSensorReading[i][0] = config.LIFT_LIMIT_SWITCH_NORMALLY[i][0] == LimitSwitchNormal.NormallyClosed;
            invertSensorReading[i][1] = config.LIFT_LIMIT_SWITCH_NORMALLY[i][1] == LimitSwitchNormal.NormallyClosed;
        }
    }

    public boolean allSidesAtBottom()
    {
        return allSidesAtLimitSwitch(Limit.BOTTOM);
    }

    public boolean allSidesAtTop()
    {
        return allSidesAtLimitSwitch(Limit.TOP);
    }

    private boolean allSidesAtLimitSwitch(Limit limit)
    {
        boolean allAtLimit = true;
        for (int index = 0; index < motors.length; index++)
        {
            allAtLimit &= isSideAtLimit(limit, index);
        }
        return allAtLimit;
    }


    private boolean isSideAtLimit(Limit limit, int index)
    {
        if(limit == Limit.BOTTOM)
        {
            return motors[index].getSensorCollection().isRevLimitSwitchClosed() ^ invertSensorReading[index][1];
        }
        return motors[index].getSensorCollection().isFwdLimitSwitchClosed() ^ invertSensorReading[index][0];
    }

    public boolean anySideAtBottom()
    {
        return anySideAtLimitSwitch(Limit.BOTTOM);
    }

    public boolean anySideAtTop()
    {
        return anySideAtLimitSwitch(Limit.TOP);
    }

    private boolean anySideAtLimitSwitch(Limit limit){
        boolean atLimit = false;
        for (int index = 0; index < motors.length; index++)
        {
            atLimit = isSideAtLimit(limit, index);
            if(atLimit)return atLimit;
        }
        return atLimit;
    }
    public void smartDashPrint(){
        SmartDashboard.putBoolean("LB", isSideAtLimit(Limit.BOTTOM, 0));
        SmartDashboard.putBoolean("LT", isSideAtLimit(Limit.TOP, 0));
        SmartDashboard.putBoolean("RB", isSideAtLimit(Limit.BOTTOM, 1));
        SmartDashboard.putBoolean("RT", isSideAtLimit(Limit.TOP, 1));
    }
}
