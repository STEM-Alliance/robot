package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.subsystems.Lift;

import edu.wpi.first.wpilibj.command.Command;

public class LiftToHeight extends Command
{
    private final Lift lift = Lift.getInstance();
    protected final double desired;

    public LiftToHeight(double desired)
    {
        requires(lift);
        this.desired = desired;
    }

    protected void initialize()
    {
        lift.setClosedLoop(desired);
    }

    protected boolean isFinished()
    {
        final boolean isClose = Math.abs(lift.getPosition() - desired) < 1.0;
        return isClose || Robot.controls.isLiftOverrideRequested();
    }

    protected void end()
    {
        lift.setOpenLoop(0.0);  // In autonomous, this holds the current position
    }
}
