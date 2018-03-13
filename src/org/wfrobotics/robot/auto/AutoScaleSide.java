package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.ScaleChoice;
import org.wfrobotics.reuse.commands.drive.AutoDrive;
import org.wfrobotics.reuse.commands.drive.DriveDistance;
import org.wfrobotics.reuse.commands.drive.TurnToHeading;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.reuse.utilities.MatchState2018.Side;
import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.config.Autonomous.POSITION;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoScaleSide extends CommandGroup
{
    public AutoScaleSide(POSITION location)
    {
        addSequential(new DriveDistance(12.0 * 24.00 + 0.0));

        if (location == POSITION.RIGHT)
        {
            addSequential(new ScaleChoice(Side.Right, new ScoreScale(true)));
        }
        else
        {
            addSequential(new ScaleChoice(Side.Left, new ScoreScale(false)));
        }
    }

    private class ScoreScale extends CommandGroup
    {
        public ScoreScale(boolean flipAngle)
        {
            addParallel(new LiftToHeight(LiftHeight.Scale.get()));
            addSequential(new TurnToHeading((flipAngle) ? -90.0 : 90.0, 3.0));
            addSequential(new TurnToHeading((flipAngle) ? -90.0 : 90.0, 3.0));
            addSequential(new AutoDrive(new HerdVector(0.5, (flipAngle) ? -90.0 : 90.0), 0.5));
            addSequential(new IntakeSet( 0.5, 1.0, true));
            addSequential(new IntakeSet( 0.0, 0.0, true));
            addSequential(new DriveDistance(12.0 * -1.0));
            addParallel(new LiftToHeight(LiftHeight.Intake.get()));
        }
    }
}
