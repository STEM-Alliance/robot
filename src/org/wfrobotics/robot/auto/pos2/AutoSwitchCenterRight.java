package org.wfrobotics.robot.auto.pos2;

import org.wfrobotics.reuse.commands.drivebasic.DriveDistance;
import org.wfrobotics.reuse.commands.drivebasic.TurnToHeading;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoSwitchCenterRight extends CommandGroup
{
    double tol = 0.5;

    public AutoSwitchCenterRight()
    {
        addSequential(new DriveDistance(12));
        addSequential(new TurnToHeading(35, tol));
        addSequential(new DriveDistance(95));
        addSequential(new TurnToHeading(0 , tol));
        addSequential(new DriveDistance(15));
        //outtake cube
        addSequential(new DriveDistance(-6));
        addSequential(new TurnToHeading(90, tol));
        addSequential(new DriveDistance(45));
        addSequential(new TurnToHeading(0, tol));
        addSequential(new DriveDistance(100));
        addSequential(new TurnToHeading(-90, tol));
        addSequential(new DriveDistance(12));
        // pickup scale cube
        addSequential(new TurnToHeading(-60, tol));
        addSequential(new DriveDistance(12 * 15));
        addSequential(new TurnToHeading(0, tol));
        //start lifting the lift
        addSequential(new DriveDistance(12 * 5));






        //        addSequential(new TurnToHeading(0, 3));
        //        addSequential(new DriveDistance(36));
        //        addSequential(new TurnToHeading(-90, 3));
        //        addSequential(new DriveDistance(12 * 13.5));





    }

    public AutoSwitchCenterRight(String name)
    {
        super(name);
    }
}
