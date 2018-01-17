
package org.wfrobotics.reuse.commands.drive.swerve;

/** Drive relative to the field. The robot's momentum is not dampened when the command ends. **/
public class AutoDriveCoast extends AutoDrive
{
    public AutoDriveCoast(double speedX, double speedY, double timeout)
    {
        super(speedX, speedY, timeout);
    }

    protected void end()
    {
        // Coast means don't stop motors here
    }
}
