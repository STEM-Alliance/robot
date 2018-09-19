package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.AutoMode;
import org.wfrobotics.reuse.commands.drive.DriveDistance;
import org.wfrobotics.robot.config.MatchState2018;
import org.wfrobotics.robot.config.MatchState2018.Side;

public class ModeSide extends AutoMode
{
    public static enum POSITION {RIGHT, CENTER, LEFT};

    private final MatchState2018 state = MatchState2018.getInstance();

    public ModeSide()
    {
        super(null);

        POSITION location = POSITION.RIGHT;  // TODO

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
