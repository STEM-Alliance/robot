package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class FieldRelative extends InstantCommand
{
    private boolean request;

    public FieldRelative(boolean enable)
    {
        requires(Robot.driveSubsystem);
        request = enable;
    }

    protected void initialize()
    {
        Robot.driveSubsystem.setFieldRelative(request);
    }
}
