package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.swerve.AutoDriveRobotRelative;
import org.wfrobotics.robot.commands.Lift;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class VisionGearDropAndBackup extends CommandGroup
{
    public VisionGearDropAndBackup()
    {
        addSequential(new VisionGear());  // In front of the gear; score it with vision

        addSequential(new WaitCommand(.5));  // Why? Technically should be autodrive until fast loop service drive pids

        // now drop it and back up
        addParallel(new Lift(Lift.MODE.DOWN));
        addSequential(new AutoDriveRobotRelative(0.2, 0, .75));

        addParallel(new Lift(Lift.MODE.DOWN));
        addSequential(new AutoDriveRobotRelative(.4, 0, .5));
    }
}
