package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.reuse.controller.Xbox.AXIS;

import edu.wpi.first.wpilibj.command.Command;

/** Sets ExampleSubsystem to a safe state */
public class DriveArcade3DPro extends Command
{
    public DriveArcade3DPro()
    {
        requires(Robot.prototypeSubsystem);
    }

    protected void execute()
    {
        double stickY;
        double stickX;
        stickX=Robot.controls.joystick.getZ();
        stickY=Robot.controls.joystick.getZ(); 
        Robot.prototypeSubsystem.setSpeed(stickX, stickY);
    }

    protected boolean isFinished()
    {
        return false;
    }
}