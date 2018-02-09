package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drivebasic.DriveDistance;
import org.wfrobotics.reuse.commands.drivebasic.TurnToHeading;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DriveTwoDistances extends CommandGroup
{
    public DriveTwoDistances()
    {
        this.addSequential(new DriveDistance(12*19+0));
        this.addSequential(new TurnToHeading(-90,1));
        this.addSequential(new DriveDistance(19.0 * 12.0));
        this.addSequential(new TurnToHeading(-180,1));
        this.addSequential(new DriveDistance(14 * 12));
        this.addSequential(new TurnToHeading(90,1));
        this.addSequential(new DriveDistance(213));
        this.addSequential(new TurnToHeading(0,1));
    }
}
