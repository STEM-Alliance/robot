package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.reuse.controller.Xbox.AXIS;

import edu.wpi.first.wpilibj.command.Command;

/** Sets ExampleSubsystem to a safe state */
public class DriveCar extends Command
{
    public DriveCar()
    {
        requires(Robot.prototypeSubsystem);
    }

    protected void execute()
    {
        double stickY;
        stickY=Robot.controls.controller.getAxis(AXIS.LEFT_Y);
        double stickX;
        stickX=Robot.controls.controller.getAxis(AXIS.RIGHT_X); 
        Robot.prototypeSubsystem.setSpeed(stickX, stickY);
    }

    protected boolean isFinished()
    {
        return false;
    }
}