package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.swerve.AutoDrive;
import org.wfrobotics.reuse.commands.drive.swerve.AutoDriveCoast;
import org.wfrobotics.robot.commands.Lift;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class VisionGearDropAndBackup extends CommandGroup
{
    public VisionGearDropAndBackup()
    {
        addSequential(new VisionGear());  // In front of the gear; score it with vision

        addSequential(new WaitCommand(.5));  // Why?

        // now drop it and back up
        addParallel(new Lift(Lift.MODE.DOWN));
        addSequential(new AutoDriveCoast(0.2, 0, 0, -1, .75));

        addParallel(new Lift(Lift.MODE.DOWN));
        addSequential(new AutoDrive(.4, 0, 0, -1, .5));
    }
}
