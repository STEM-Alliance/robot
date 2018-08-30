package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.config.LiftHeight;
import org.wfrobotics.robot.subsystems.LiftSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class LiftOpenLoop extends Command
{
    private final double deadbandPercent = 0.1;
    private final RobotState state = RobotState.getInstance();
    private final LiftSubsystem lift = LiftSubsystem.getInstance();

    public LiftOpenLoop()
    {
        requires(lift);
    }

    protected void execute()
    {
        double setpoint = Robot.controls.getLiftStick();

        if (Math.abs(setpoint) < deadbandPercent)
        {
            setpoint = 0.0;
        }
        else if (state.liftHeightInches < LiftHeight.Intake.get() + 4.0 && setpoint < 0.0)
        {
            setpoint /= 4.0;
        }
        else if (state.liftHeightInches > LiftHeight.Scale.get() - 4.0 && setpoint > 0.0)
        {
            setpoint /= 4.0;
        }
        lift.setOpenLoop(setpoint);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
