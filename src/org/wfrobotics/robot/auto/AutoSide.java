package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.DriveDistance;
import org.wfrobotics.reuse.utilities.MatchState2018;
import org.wfrobotics.reuse.utilities.MatchState2018.Side;
import org.wfrobotics.robot.config.Autonomous.POSITION;
import org.wfrobotics.robot.path.ModeScale;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoSide extends CommandGroup
{
    private final MatchState2018 state = MatchState2018.getInstance();

    public AutoSide(POSITION location)
    {
        if (state.Scale == Side.Unknown)
        {
            addSequential(new DriveDistance(12.0 * 3.0));
        }
        else if (scaleOnThisSide(location))
        {
            addSequential(new ModeScale(location));
        }
        else
        {
            addSequential(new AutoOppisitScalse(location));
        }
    }

    private boolean scaleOnThisSide(POSITION location)
    {
        return (location == POSITION.RIGHT && state.Scale == Side.Right) || (location == POSITION.LEFT && state.Scale == Side.Left);
    }
}
