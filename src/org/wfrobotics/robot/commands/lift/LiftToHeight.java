package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftToHeight extends Command
{
    private final RobotState state = RobotState.getInstance();
    protected final double desired;

    public LiftToHeight(double desired)
    {
        requires(Robot.liftSubsystem);
        this.desired = desired;
    }

    protected void initialize()
    {
        SmartDashboard.putString("Lift", getClass().getSimpleName());
        Robot.liftSubsystem.goToHeightInit(desired);
    }

    protected boolean isFinished()
    {
        return Math.abs(state.liftHeightInches - desired) < 2 || Math.abs(Robot.controls.getLiftStick()) > .15;
    }
}
