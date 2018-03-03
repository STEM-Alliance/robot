package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drivebasic.DriveDistance;
import org.wfrobotics.reuse.commands.drivebasic.TurnToHeading;
import org.wfrobotics.robot.commands.intake.WristToggle;
import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.config.Autonomous.POSITION;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoOppisitScalse extends CommandGroup
{
    boolean direcion;
    public AutoOppisitScalse(POSITION location)
    {
        addSequential(new DriveDistance(12.0 * 19.0));
        addSequential(new TurnToHeading((location == POSITION.LEFT) ? 90.0 : -90.0, 5.0));
        addSequential(new TurnToHeading((location == POSITION.LEFT) ? 90.0 : -90.0, 5.0));
        addSequential(new DriveDistance(12.0 * 18.0));

        addParallel(new LiftToHeight(LiftHeight.Scale.get()));
        addSequential(new TurnToHeading((location == POSITION.LEFT) ? -20.0: 20.0, 5.0));
        addSequential(new TurnToHeading((location == POSITION.LEFT) ? -20.0: 20.0, 5.0));


        addSequential(new DriveDistance(12.0 * 5.0));
        //        addSequential(new DriveDistance(12 * 3));
        addSequential(new WristToggle());
        addSequential(new IntakeSet(0.3, 0.5, true));
        //        addSequential(new SmartOutake());
        //        addSequential(new DriveDistance(12.0 * -3.0));
    }
}

