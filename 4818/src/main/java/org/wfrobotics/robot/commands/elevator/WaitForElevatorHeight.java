package org.wfrobotics.robot.commands.elevator;

import org.wfrobotics.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.command.Command;

/** Delay until lift is at height */
public class WaitForElevatorHeight extends Command
{
    private final Elevator elevator = Elevator.getInstance();
    private final double threshold;
    private final boolean wantAtLeast;

    public WaitForElevatorHeight(double inchesOffGround, boolean atOrAbove)
    {
        threshold = inchesOffGround;
        wantAtLeast = atOrAbove;
    }

    protected boolean isFinished()
    {
        final double height = elevator.getPosition();
        return (wantAtLeast) ?  height > threshold :  height < threshold;
    }
}
