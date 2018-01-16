package org.wfrobotics.reuse.commands.differential;

import org.wfrobotics.reuse.subsystems.drive.DifferentialService;

import edu.wpi.first.wpilibj.command.Command;

public class DriveTank extends Command
{
    protected DifferentialService<?> driveHelper;

    public DriveTank(DifferentialService<?> helper)
    {
        driveHelper = helper;
        requires(driveHelper.getDrive());
    }

    protected void execute()
    {
        // TODO drive with IO
    }

    protected boolean isFinished()
    {
        return false;
    }
}
