package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class ConveyorOff extends Command
{
    public ConveyorOff()
    {
        requires(Robot.augerSubsystem);
    }
    
    protected void initialize()
    {
        Robot.augerSubsystem.setSpeed(0);
    }
    protected boolean isFinished()
    {
        return false;
    }
}
