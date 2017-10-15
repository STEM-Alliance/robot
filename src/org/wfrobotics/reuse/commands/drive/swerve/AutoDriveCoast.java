
package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;

public class AutoDriveCoast extends AutoDrive
{
    public AutoDriveCoast(double speedX, double speedY, double speedR, double timeout)
    {
        super(speedX, speedY, speedR, SwerveSignal.HEADING_IGNORE, timeout);
    }

    public AutoDriveCoast(double speedX, double speedY, double speedR, double angle, double timeout)
    {
        super(speedX, speedY, speedR, angle, timeout);
    }

    protected void end()
    {
        // Coast means don't stop motors here
    }
}
