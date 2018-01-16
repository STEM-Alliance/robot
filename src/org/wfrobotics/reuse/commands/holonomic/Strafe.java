package org.wfrobotics.reuse.commands.holonomic;

import org.wfrobotics.reuse.subsystems.drive.HolonomicService;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;

import edu.wpi.first.wpilibj.command.Command;

public class Strafe extends Command
{
    protected HolonomicService<?> driveHelper;
    final SwerveSignal s;

    public Strafe(HolonomicService<?> helper, double xSpeed, double timeout)
    {
        driveHelper = helper;
        requires(driveHelper.getDrive());
        s = new SwerveSignal(new HerdVector(xSpeed, 0));
        setTimeout(timeout);
    }

    protected void execute()
    {
        driveHelper.getDrive().driveWithHeading(s);
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }

    protected void end()
    {
        driveHelper.getDrive().driveWithHeading(new SwerveSignal(new HerdVector(0, 0)));
    }
}
