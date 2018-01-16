package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.reuse.subsystems.drive.DriveService;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class Shift extends InstantCommand
{
    DriveService<?> driveHelper;
    private boolean request;

    public Shift(DriveService<?> helper, boolean highGear)
    {
        driveHelper = helper;
        requires(driveHelper.getDrive());
        request = highGear;
    }

    protected void initialize()
    {
        driveHelper.getDrive().setGear(request);
    }
}
