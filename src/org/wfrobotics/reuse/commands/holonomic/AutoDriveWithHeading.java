package org.wfrobotics.reuse.commands.holonomic;

import org.wfrobotics.reuse.subsystems.drive.HolonomicService;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;

import edu.wpi.first.wpilibj.command.Command;

/** Drive while letting swerve simultaneously oversee turning to an angle **/
public class AutoDriveWithHeading extends Command
{
    protected HolonomicService<?> driveHelper;
    protected final SwerveSignal s;

    public AutoDriveWithHeading(HolonomicService<?> helper, double speedX, double speedY, double angle, double timeout)
    {
        driveHelper = helper;
        requires(driveHelper.getDrive());
        s = new SwerveSignal(new SwerveSignal(new HerdVector(speedX, speedY), 0, angle));
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