package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.reuse.subsystems.drive.DriveService;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class GyroZero extends InstantCommand
{
    DriveService<?> driveHelper;

    public GyroZero(DriveService<?> helper)
    {
        driveHelper = helper;
        requires(driveHelper.getDrive());
    }

    protected void initialize()
    {
        driveHelper.getDrive().zeroGyro();
    }
}
