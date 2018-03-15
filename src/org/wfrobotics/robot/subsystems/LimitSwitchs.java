package org.wfrobotics.robot.subsystems;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class LimitSwitchs
{
    private enum Limit
    {
        BOTTOM,
        TOP
    }

    private class LimitSwitch
    {
        private final TalonSRX motor;
        private final boolean[] invert;

        public LimitSwitch(TalonSRX motor, LimitSwitchNormal[] notSetState)
        {
            invert = new boolean[2];
            this.motor = motor;

            this.motor.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, notSetState[0], 10);
            this.motor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, notSetState[1], 10);
            this.motor.overrideLimitSwitchesEnable(true);

            invert[0] = notSetState[0] == LimitSwitchNormal.NormallyClosed;
            invert[1] = notSetState[1] == LimitSwitchNormal.NormallyClosed;
        }

        public boolean isSet(Limit limit)
        {
            if(limit == Limit.BOTTOM)
            {
                return motor.getSensorCollection().isRevLimitSwitchClosed() ^ invert[1];
            }
            return motor.getSensorCollection().isFwdLimitSwitchClosed() ^ invert[0];
        }
    }

    private final LimitSwitch[] limitSwitch;

    public LimitSwitchs(TalonSRX[] motors, LimitSwitchNormal[][] notSetState)
    {
        limitSwitch = new LimitSwitch[motors.length];

        for(int index = 0; index < motors.length; index++)
        {
            limitSwitch[index] = new LimitSwitch(motors[index], notSetState[index]);
        }
    }

    public boolean atBottomAll()
    {
        return allSet(Limit.BOTTOM);
    }

    public boolean atTopAll()
    {
        return allSet(Limit.TOP);
    }

    public boolean atBottomAny()
    {
        return anySet(Limit.BOTTOM);
    }

    public boolean atTopAny()
    {
        return anySet(Limit.TOP);
    }

    public boolean[][] dump()
    {
        boolean[][] buffer = new boolean[limitSwitch.length][2];

        for(int index = 0; index < limitSwitch.length; index++)
        {
            buffer[index][0] = limitSwitch[index].isSet(Limit.BOTTOM);
            buffer[index][1] = limitSwitch[index].isSet(Limit.TOP);
        }
        return buffer;
    }

    private boolean allSet(Limit limit)
    {
        for (int index = 0; index < limitSwitch.length; index++)
        {
            if(!limitSwitch[index].isSet(limit))
            {
                return false;
            }
        }
        return true;
    }

    private boolean anySet(Limit limit)
    {
        for (int index = 0; index < limitSwitch.length; index++)
        {
            if(limitSwitch[index].isSet(limit))
            {
                return true;
            }
        }
        return false;
    }
}
