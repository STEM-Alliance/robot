package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SmartWrist extends Command
{
    private final RobotState state = RobotState.getInstance();
    private final Wrist wrist;

    public SmartWrist()
    {
        wrist = Robot.wrist;
        requires(wrist);
    }

    protected void initialize()
    {
        SmartDashboard.putString("Wrist", this.getClass().getSimpleName());
    }

    protected void execute()
    {
        double commanded = Robot.controls.getIntakeLift();

        if (Math.abs(commanded) < 0.1  && Math.abs(state.robotVelocity.getMag()) > 0.1 && Math.abs(state.liftHeightInches)
                                        < 1 && state.robotHasCube)
        {
            wrist.setPosition(1.0);
        }
        else
        {
            wrist.setSpeed(commanded);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
