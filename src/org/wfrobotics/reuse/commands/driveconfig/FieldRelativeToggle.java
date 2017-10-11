package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class FieldRelativeToggle extends InstantCommand
{
    public FieldRelativeToggle()
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize()
    {
        Robot.driveSubsystem.setFieldRelative(!Robot.driveSubsystem.getFieldRelative());
    }
}
