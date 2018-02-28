package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.ScaleChoice;
import org.wfrobotics.reuse.commands.drivebasic.DriveDistance;
import org.wfrobotics.reuse.commands.drivebasic.TurnToHeading;
import org.wfrobotics.reuse.utilities.MatchState2018.Side;
import org.wfrobotics.robot.commands.intake.SmartOutake;
import org.wfrobotics.robot.commands.intake.WristToggle;
import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.config.Autonomous.POSITION;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoScaleSide extends CommandGroup
{
    public AutoScaleSide(POSITION location)
    {
        addSequential(new DriveDistance(12 * 29 + 0));

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
            addSequential(new TurnToHeading((flipAngle) ? -90 : 90, 0.2));
            addSequential(new DriveDistance(12 * 3));
            addSequential(new WristToggle());
            addSequential(new SmartOutake());
            addSequential(new DriveDistance(12 * -3));
        }
    }

}
