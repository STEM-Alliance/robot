package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.subsystems.drive.DriveService;
import org.wfrobotics.reuse.utilities.HerdVector;

/** Drive relative to the robot's orientation **/
public class AutoDriveRobotRelative extends AutoDrive
{
    public AutoDriveRobotRelative(DriveService<?> helper, HerdVector vector, double timeout)
    {
        super(helper, vector, timeout);
    }

    protected void execute()
    {
        driveHelper.getDrive().driveBasic(robotRelative);
    }
}
