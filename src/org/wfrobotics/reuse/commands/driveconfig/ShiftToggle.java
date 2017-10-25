package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class ShiftToggle extends InstantCommand
{
    public ShiftToggle()
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize()
    {
        Robot.driveSubsystem.requestHighGear = !Robot.driveSubsystem.requestHighGear;
    }
}
