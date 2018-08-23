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
        return Math.abs(state.liftHeightInches - desired) < 2.0 || Math.abs(Robot.controls.getLiftStick()) > .15;
    }
}
