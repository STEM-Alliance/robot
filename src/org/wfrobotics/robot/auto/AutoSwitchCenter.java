package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drivebasic.DriveDistance;
import org.wfrobotics.reuse.commands.drivebasic.TurnToHeading;
import org.wfrobotics.robot.commands.lift.AutoZero;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoSwitchCenter extends CommandGroup
{
    double tol = 0.75;
    double angleFirstTurn = 40.0;

    /**
     * @boolean true is 35 (Going to the Right)
     *          false is -35 (going to the Left)
     */
    public AutoSwitchCenter(boolean direction)
    {
        // Score Cube
        addParallel(new AutoZero());
        addSequential(new DriveDistance(12.0));
        addSequential(new TurnToHeading((direction) ? angleFirstTurn : -angleFirstTurn, tol));
        addSequential(new DriveDistance(100.0));
        addSequential(new TurnToHeading(0.0, tol));
        //        addSequential(new DriveIntakeSensors(0.0, 1.0));
        addSequential(new DriveDistance(15.0));
        addSequential(new IntakeSet(0.6, 0.5, true));

        // Get Around Switch
        addSequential(new DriveDistance(-12.0));
        addSequential(new TurnToHeading((direction) ? 90.0 : -90.0, tol));
        addSequential(new DriveDistance(12.0 * 4.0));
        addSequential(new TurnToHeading(0.0, tol));
        addSequential(new DriveDistance(12.0 * 6.0));

        //        addSequential(new DriveDistance(-6));
        //        addSequential(new TurnToHeading(-90, tol));
        //        addSequential(new DriveDistance(45));
        //        addSequential(new TurnToHeading(0, tol));
        //        addSequential(new DriveDistance(100));
        //        addSequential(new TurnToHeading(90, tol));
        //        addSequential(new DriveDistance(12));
        //        // pickup scale cube
        //        addSequential(new TurnToHeading(60, tol));
        //        addSequential(new DriveDistance(12 * 15));
        //        addSequential(new TurnToHeading(0, tol));
        //        //start lifting the lift
        //        addSequential(new DriveDistance(12 * 5));
    }
}
