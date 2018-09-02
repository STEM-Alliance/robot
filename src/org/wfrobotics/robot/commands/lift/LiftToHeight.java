package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.LiftSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class LiftToHeight extends Command
{
    private final RobotState state = RobotState.getInstance();
    private final LiftSubsystem lift = LiftSubsystem.getInstance();
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
        final boolean isClose = Math.abs(state.liftHeightInches - desired) < 1.0;
        return isClose || Robot.controls.isLiftOverrideRequested();
    }

    protected void end()
    {
        lift.setOpenLoop(0.0);  // In autonomous, this holds the current position
    }
}
