package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.reuse.commands.drive.swerve.DriveSwerve;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class FieldRelativeToggle extends InstantCommand
{
    protected void initialize()
    {
        DriveSwerve.FIELD_RELATIVE = !DriveSwerve.FIELD_RELATIVE;
    }
}
