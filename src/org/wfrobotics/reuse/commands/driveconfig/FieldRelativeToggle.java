package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.reuse.commands.holonomic.DriveSwerve;

import edu.wpi.first.wpilibj.command.InstantCommand;

// TODO Work with any drive type

public class FieldRelativeToggle extends InstantCommand
{
    protected void initialize()
    {
        DriveSwerve.FIELD_RELATIVE = !DriveSwerve.FIELD_RELATIVE;
    }
}
