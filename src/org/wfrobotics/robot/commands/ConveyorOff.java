package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class ConveyorOff extends Command
{
    protected void initialize()
    {
        Robot.augerSubsystem.setSpeed(0);
    }
    protected boolean isFinished()
    {
        return false;
    }
}
