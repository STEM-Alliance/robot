package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;

public class LiftToHeightAndHold extends LiftToHeight
{
    public LiftToHeightAndHold(double desired)
    {
        super(desired);
    }

    protected boolean isFinished()
    {
        return Math.abs(Robot.controls.getLiftStick()) > .15;
    }
}
