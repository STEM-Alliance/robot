package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.DriveDistance;
import org.wfrobotics.reuse.commands.wrapper.AutoMode;
import org.wfrobotics.robot.config.Auto;
import org.wfrobotics.robot.config.Auto.POSITION;
import org.wfrobotics.robot.config.MatchState2018;
import org.wfrobotics.robot.config.MatchState2018.Side;

public class ModeSide extends AutoMode
{
    private final MatchState2018 state = MatchState2018.getInstance();

    // TODO Refactor to flatten? Should branching use a wrapper group or not?

    public ModeSide()
    {
        POSITION location = Auto.positions.get();

        if (state.Scale == Side.Unknown)
        {
            addSequential(new DriveDistance(12.0 * 3.0));
        }
        else if (scaleOnThisSide(location))
        {
            addSequential(new ModeScale());
        }
        else
        {
            addSequential(new ModeOppisitScalse());
        }
    }

    private boolean scaleOnThisSide(POSITION location)
    {
        return (location == POSITION.RIGHT && state.Scale == Side.Right) || (location == POSITION.LEFT && state.Scale == Side.Left);
    }
}
