
package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.utilities.HerdVector;

/** Drive relative to the field. The robot's momentum is not dampened when the command ends. **/
public class AutoDriveCoast extends AutoDrive
{
    public AutoDriveCoast(HerdVector vector, double timeout)
    {
        super(vector, timeout);
    }

    protected void end()
    {
        // Coast means don't stop motors here
    }
}
