package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.reuse.controller.Xbox.AXIS;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.command.Command;

/** Sets ExampleSubsystem to a safe state */
public class DriveRocketLeague extends Command
{
    public DriveRocketLeague()
    {
        requires(Robot.prototypeSubsystem);
    }

    protected void execute()
    {
        double triggerL;
        double triggerR;
        triggerL=Robot.controls.controller.getTrigger(Hand.kLeft);
        triggerR=Robot.controls.controller.getTrigger(Hand.kRight);
        double speed = triggerR-triggerL;
        double stickX;
        stickX=Robot.controls.controller.getAxis(AXIS.LEFT_X);
        Robot.prototypeSubsystem.setSpeed(stickX, speed);
    }

    protected boolean isFinished()
    {
        return false;
    }
}