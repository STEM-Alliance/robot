package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;

/** Delay until wrist is in position */
public class WaitForWristAngle extends Command
{
    private final RobotState state;
    private final double threshold;
    private final boolean wantAtLeast;

    public WaitForWristAngle(double degreesFromBottom, boolean atOrAbove)
    {
        state = RobotState.getInstance();
        threshold = degreesFromBottom;
        wantAtLeast = atOrAbove;
    }

    protected boolean isFinished()
    {
        final double angle = state.wristAngleDegrees;
        return (wantAtLeast) ?  angle > threshold :  angle < threshold;
    }
}
