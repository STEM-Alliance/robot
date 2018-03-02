package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.SwitchChoice;
import org.wfrobotics.reuse.commands.drivebasic.DriveDistance;
import org.wfrobotics.reuse.commands.drivebasic.TurnToHeading;
import org.wfrobotics.reuse.utilities.MatchState2018.Side;
import org.wfrobotics.robot.config.Autonomous.POSITION;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoSwitchSide extends CommandGroup
{
    public AutoSwitchSide(POSITION location)
    {
        addSequential(new DriveDistance(12 * 11 + 3));
        if (location == POSITION.RIGHT)
        {
            addSequential(new SwitchChoice(Side.Right, new ScoreSwitch(false)));
        }
        else
        {
            addSequential(new SwitchChoice(Side.Left, new ScoreSwitch(true)));
        }
    }

    private class ScoreSwitch extends CommandGroup
    {
        public ScoreSwitch(boolean flipAngle)
        {
            addSequential(new TurnToHeading((flipAngle) ? 90 : -90, 0.2));
            addSequential(new TurnToHeading((flipAngle) ? 90 : -90, 0.2));
            addSequential(new DriveDistance(18));
            addSequential(new IntakeSet(0.8, 1.0, true));
            // TODO move back into normal path
        }
    }
}
