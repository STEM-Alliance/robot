package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.SwitchChoice;
import org.wfrobotics.reuse.commands.drivebasic.DriveDistance;
import org.wfrobotics.reuse.commands.drivebasic.TurnToHeading;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoSwitchCenter extends CommandGroup
{
    double tol = 5.0;
    double angleFirstTurn = 40.0;

    public AutoSwitchCenter()
    {
        // Score Cube
        addSequential(new DriveDistance(12.0));
        addSequential(new SwitchChoice(new TurnToHeading(angleFirstTurn, tol), new TurnToHeading(-angleFirstTurn, tol)));
        addSequential(new SwitchChoice(new TurnToHeading(angleFirstTurn, tol), new TurnToHeading(-angleFirstTurn, tol)));
        addSequential(new DriveDistance(100.0));
        addSequential(new TurnToHeading(0.0, tol));
        addSequential(new TurnToHeading(0.0, tol));
        //        addSequential(new DriveIntakeSensors(0.0, 1.0));
        addSequential(new DriveDistance(15.0));
        addSequential(new IntakeSet(0.4, 0.5, true));

        // Get Around Switch
        addSequential(new DriveDistance(-12.0));
        addSequential(new SwitchChoice(new TurnToHeading(90.0, tol), new TurnToHeading(-90.0, tol)));
        addSequential(new SwitchChoice(new TurnToHeading(90.0, tol), new TurnToHeading(-90.0, tol)));
        addSequential(new DriveDistance(12.0 * 4.0));
        addSequential(new TurnToHeading(0.0, tol));
        addSequential(new TurnToHeading(0.0, tol));
    }
}
