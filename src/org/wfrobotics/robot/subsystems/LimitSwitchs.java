package org.wfrobotics.robot.subsystems;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class LimitSwitchs
{
    private LimitSwitch[] limitSwitch;
    private enum Limit
    {
        BOTTOM,
        TOP
    }
    public LimitSwitchs( TalonSRX[] motors,  LimitSwitchNormal[][] config){
        //        this.motors = motors;
        //        final int kTimeout = 10;
        limitSwitch = new LimitSwitch[motors.length];
        for(int i = 0; i < motors.length; i++){
            //                motors[i].configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, config[i][0], kTimeout);
            //                motors[i].configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, config[i][1], kTimeout);
            //            motors[i].overrideLimitSwitchesEnable(true);
            //            invertSensorReading[i][0] = config[i][0] == LimitSwitchNormal.NormallyClosed;
            //            invertSensorReading[i][1] = config[i][1] == LimitSwitchNormal.NormallyClosed;
            limitSwitch[i] = new LimitSwitch(motors[i], config[i]);
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
        /*for (int index = 0; index < motors.length; index++)
        {
            allAtLimit &= isSideAtLimit(limit, index);
        }
        return allAtLimit;*/
        for (int i = 0; i < limitSwitch.length; i++){
            allAtLimit = limitSwitch[i].isSideAtLimit(limit);
            if(!allAtLimit)return allAtLimit;
        }
        return allAtLimit;
    }


    /*private boolean isSideAtLimit(Limit limit, int index)
    {
        if(limit == Limit.BOTTOM)
        {
            return motors[index].getSensorCollection().isRevLimitSwitchClosed() ^ invertSensorReading[index][1];
        }
        return motors[index].getSensorCollection().isFwdLimitSwitchClosed() ^ invertSensorReading[index][0];
    }*/

    public boolean anySideAtBottom()
    {
        return anySideAtLimitSwitch(Limit.BOTTOM);
    }

    public boolean anySideAtTop()
    {
        return anySideAtLimitSwitch(Limit.TOP);
    }

    private boolean anySideAtLimitSwitch(Limit limit){
        /*boolean atLimit = false;
        for (int index = 0; index < motors.length; index++)
        {
            atLimit = isSideAtLimit(limit, index);
            if(atLimit)return atLimit;
        }
        return atLimit;*/
        boolean atLimit = false;
        for (int i = 0; i < limitSwitch.length; i++)
        {
            atLimit = limitSwitch[i].isSideAtLimit(limit);
            if(atLimit)return atLimit;
        }
        return atLimit;
    }

    public boolean[] smartDashPrint(){
        /*SmartDashboard.putBoolean("LB", isSideAtLimit(Limit.BOTTOM, 0));
        SmartDashboard.putBoolean("LT", isSideAtLimit(Limit.TOP, 0));
        SmartDashboard.putBoolean("RB", isSideAtLimit(Limit.BOTTOM, 1));
        SmartDashboard.putBoolean("RT", isSideAtLimit(Limit.TOP, 1));*/
        boolean[] smartDash = new boolean[limitSwitch.length * 2];
        int i = 0;
        while( i < limitSwitch.length){
            smartDash[i] = limitSwitch[i].isSideAtLimit(Limit.BOTTOM);
            smartDash[i + 1] = limitSwitch[i].isSideAtLimit(Limit.TOP);
            i += 2;
        }
        return smartDash;
    }

    private class LimitSwitch{
        private TalonSRX motor;
        private int kTimeout = 10;
        private final boolean[] invertSensorReading = new boolean[2];

        public LimitSwitch(TalonSRX motor,  LimitSwitchNormal[] config){
            this.motor.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, config[0], kTimeout);
            this.motor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, config[1], kTimeout);
            this.motor.overrideLimitSwitchesEnable(true);
            invertSensorReading[0] = config[0] == LimitSwitchNormal.NormallyClosed;
            invertSensorReading[1] = config[1] == LimitSwitchNormal.NormallyClosed;
        }

        public boolean isSideAtLimit(Limit limit){
            if(limit == Limit.BOTTOM)
            {
                return motor.getSensorCollection().isRevLimitSwitchClosed() ^ invertSensorReading[1];
            }
            return motor.getSensorCollection().isFwdLimitSwitchClosed() ^ invertSensorReading[0];
        }

    }
}
