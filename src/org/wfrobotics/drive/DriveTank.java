package org.wfrobotics.drive;

import edu.wpi.first.wpilibj.command.Command;

public class DriveTank extends Command
{
    protected DifferentialLocator<?> driveHelper;

    public DriveTank(DifferentialLocator<?> helper)
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
