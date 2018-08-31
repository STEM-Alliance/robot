package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;

/** Delay until lift is at height */
public class WaitForLiftHeight extends Command
{
    private final RobotState state = RobotState.getInstance();
    private final double threshold;
    private final boolean wantAtLeast;

    public WaitForLiftHeight(double inchesOffGround, boolean atOrAbove)
    {
        threshold = inchesOffGround;
        wantAtLeast = atOrAbove;
    }

    protected boolean isFinished()
    {
        final double height = state.liftHeightInches;
        return (wantAtLeast) ?  height > threshold :  height < threshold;
    }
}
