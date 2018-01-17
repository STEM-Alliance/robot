package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

/** Drive relative to the robot's orientation **/
public class AutoDriveRobotRelative extends AutoDrive
{
    public AutoDriveRobotRelative(HerdVector vector, double timeout)
    {
        super(vector, timeout);
    }

    protected void execute()
    {
        Robot.driveService.driveBasic(robotRelative);
    }
}
