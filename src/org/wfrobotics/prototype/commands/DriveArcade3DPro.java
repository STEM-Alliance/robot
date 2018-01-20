package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.reuse.controller.Xbox.AXIS;

import edu.wpi.first.wpilibj.GenericHID.Hand;
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
        double stickX;
        stickX=Robot.controls.joystick.getX(Hand.kRight);
        double stickY;
        stickY=Robot.controls.joystick.getY(Hand.kRight); 
        //stickY=0-stickY;
        Robot.prototypeSubsystem.setSpeed(stickX, stickY);
    }

    protected boolean isFinished()
    {
        return false;
    }
}