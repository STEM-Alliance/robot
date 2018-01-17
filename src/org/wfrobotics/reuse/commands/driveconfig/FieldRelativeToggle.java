package org.wfrobotics.reuse.commands.driveconfig;

import edu.wpi.first.wpilibj.command.InstantCommand;

// TODO Work with any drive type

public class FieldRelativeToggle extends InstantCommand
{
    public static boolean FIELD_RELATIVE = true;  // Toggle with button, need if gyro breaks

    protected void initialize()
    {
        FIELD_RELATIVE = !FIELD_RELATIVE;
    }
}
