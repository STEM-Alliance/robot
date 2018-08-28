package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.DriveDistance;
import org.wfrobotics.robot.auto.MatchState2018.Side;
import org.wfrobotics.robot.commands.lift.AutoZero;
import org.wfrobotics.robot.config.Autonomous.POSITION;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoSide extends CommandGroup
{
    private final MatchState2018 state = MatchState2018.getInstance();

    public AutoSide(POSITION location)
    {
        addParallel(new AutoZero());

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
            addSequential(new ModeOppisitScalse(location));
        }
    }

    private boolean scaleOnThisSide(POSITION location)
    {
        return (location == POSITION.RIGHT && state.Scale == Side.Right) || (location == POSITION.LEFT && state.Scale == Side.Left);
    }
}
