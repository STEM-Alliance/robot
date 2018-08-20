package org.wfrobotics.robot.commands.lift;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class LiftAutoZeroThenPercentVoltage extends ConditionalCommand
{
    public LiftAutoZeroThenPercentVoltage()
    {
        //        if(Robot.controls.getLiftStick() < .1)
        //        {
        //            super(new LiftToHeight(Robot.liftSubsystem.getLiftHeight()), new AutoZero());
        //        }
        //        else
        //        {
        super(new LiftPercentVoltage(), new AutoZero());
        //        }

    }

    protected boolean condition()
    {
        boolean val = LiftGoHome.everZeroed();
        return val;
    }
}
