package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.subsystems.Lift;

import edu.wpi.first.wpilibj.command.Command;

/** Delay until lift is at height */
public class WaitForLiftHeight extends Command
{
    private final Lift lift = Lift.getInstance();
    private final double threshold;
    private final boolean wantAtLeast;

    public WaitForLiftHeight(double inchesOffGround, boolean atOrAbove)
    {
        threshold = inchesOffGround;
        wantAtLeast = atOrAbove;
    }

    protected boolean isFinished()
    {
        final double height = lift.getPosition();
        return (wantAtLeast) ?  height > threshold :  height < threshold;
    }
}
