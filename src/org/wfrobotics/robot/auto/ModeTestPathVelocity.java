package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.DrivePath;
import org.wfrobotics.reuse.commands.driveconfig.ResetPose;
import org.wfrobotics.reuse.config.PathContainer;
import org.wfrobotics.robot.paths.SortOfDriveDistance;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class ModeTestPathVelocity extends CommandGroup
{
    public ModeTestPathVelocity()
    {
        PathContainer path = new SortOfDriveDistance();

        this.addSequential(new ResetPose(path));
        this.addSequential(new DrivePath(path));
        this.addSequential(new WaitCommand(2.0));
    }
}
