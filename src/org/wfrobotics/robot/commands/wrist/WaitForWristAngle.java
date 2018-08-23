package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;

/** Delay until wrist is in position */
public class WaitForWristAngle extends Command
{
    private final RobotState state = RobotState.getInstance();
    private final double threshold;
    private final boolean wantdirectionUp;

    public WaitForWristAngle(double thresholdDegrees, boolean wantPastDirectionUp)
    {
        threshold = thresholdDegrees;
        wantdirectionUp = wantPastDirectionUp;
    }

    protected boolean isFinished()
    {
        final double angle = state.wristAngleDegrees;
        return (wantdirectionUp) ?  angle > threshold :  angle < threshold;
    }
}
