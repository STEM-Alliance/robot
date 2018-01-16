package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.reuse.subsystems.drive.DriveService;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class Brake extends InstantCommand
{
    DriveService<?> driveHelper;
    private boolean request;

    public Brake(DriveService<?> helper, boolean enable)
    {
        driveHelper = helper;
        requires(driveHelper.getDrive());
        request = enable;
    }

    protected void initialize()
    {
        driveHelper.getDrive().setBrake(request);
    }
}
