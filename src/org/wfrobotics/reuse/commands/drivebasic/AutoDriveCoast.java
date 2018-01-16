
package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.subsystems.drive.DriveService;
import org.wfrobotics.reuse.utilities.HerdVector;

/** Drive relative to the field. The robot's momentum is not dampened when the command ends. **/
public class AutoDriveCoast extends AutoDrive
{
    public AutoDriveCoast(DriveService<?> helper, HerdVector vector, double timeout)
    {
        super(helper, vector, timeout);
    }

    protected void end()
    {
        // Coast means don't stop motors here
    }
}
