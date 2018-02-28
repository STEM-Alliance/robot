package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.utilities.MatchState2018;
import org.wfrobotics.reuse.utilities.MatchState2018.Side;
import org.wfrobotics.robot.commands.lift.AutoZero;
import org.wfrobotics.robot.commands.lift.LiftGoHome;
import org.wfrobotics.robot.config.Autonomous.POSITION;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoSide extends CommandGroup
{
    private final MatchState2018 state = MatchState2018.getInstance();

    public AutoSide(POSITION location)
    {
        addParallel(new AutoZero());
        if (scaleOnThisSide(location))
        {
            addSequential(new AutoScaleSide(location));
        }
        else if (switchOnThisSide(location))
        {
            addSequential(new AutoSwitchSide(location));
        }
        else
        {
            addSequential(new AutoOppisitScalse(location));
        }
        addSequential(new LiftGoHome(-0.2, 15.0));

        //        addSequential(new DriveDistance(12 * 11 + 3));
        //        if (location == POSITION.RIGHT)
        //        {
        //            addSequential(new SwitchChoice(Side.Right, new ScoreSwitch(false)));
        //        }
        //        else
        //        {
        //            addSequential(new SwitchChoice(Side.Left, new ScoreSwitch(true)));
        //        }
        //
        //
        //
        //
        //        addSequential(new DriveDistance(12 * 11 + 3));
        //        addSequential(new DriveDistance(12 * 17 + 9));
        //
        //        addParallel(new LiftToHeight(LiftHeight.Scale.get()));
        //
        //
        //        if (location == POSITION.RIGHT)
        //        {
        //            addSequential(new ScaleChoice(Side.Right, new ScoreScale(true)));
        //        }
        //        else
        //        {
        //            addSequential(new ScaleChoice(Side.Left, new ScoreScale(false)));
        //        }
    }

    private boolean scaleOnThisSide(POSITION location)
    {
        state.update();
        return (location == POSITION.RIGHT && state.Scale == Side.Right) || (location == POSITION.LEFT && state.Scale == Side.Left);
    }

    private boolean switchOnThisSide(POSITION location)
    {
        state.update();
        return (location == POSITION.RIGHT && state.SwitchNear == Side.Right) || (location == POSITION.LEFT && state.SwitchNear == Side.Left);
    }

    //    private class ScoreSwitch extends CommandGroup
    //    {
    //        public ScoreSwitch(boolean flipAngle)
    //        {
    //            addSequential(new TurnToHeading((flipAngle) ? 90 : -90, 0.2));
    //            addSequential(new DriveDistance(18));
    //            addSequential(new IntakeSet(0.8, 1.0, true));
    //            // TODO move back into normal path
    //        }
    //    }
    //
    //    private class ScoreScale extends CommandGroup
    //    {
    //        public ScoreScale(boolean flipAngle)
    //        {
    //            addSequential(new TurnToHeading((flipAngle) ? -90 : 90, 0.2));
    //            addSequential(new DriveDistance(12 * 3));
    //            addSequential(new WristToggle());
    //            addSequential(new SmartOutake());
    //            addSequential(new DriveDistance(12 * -3));
    //        }
    //    }
}
