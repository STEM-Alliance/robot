package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.config.IO;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftToHeight extends Command
{
    private final double desired;

    private final RobotState state = RobotState.getInstance();
    private final IO io;

    public LiftToHeight(double desired)
    {
        requires(Robot.liftSubsystem);
        io = Robot.controls;
        this.desired = desired;
    }

    protected void initialize()
    {
        SmartDashboard.putString("Lift", getClass().getSimpleName());
        Robot.liftSubsystem.goToHeightInit(desired);
    }

    protected boolean isFinished()
    {
        return Math.abs(desired - state.liftHeightInches) < .1 || Math.abs(io.getLiftStick()) > .15;
    }
}
