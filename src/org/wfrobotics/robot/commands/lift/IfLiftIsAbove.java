package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IfLiftIsAbove extends ConditionalCommand
{
    private final RobotState state = RobotState.getInstance();
    private final double threshold;

    public IfLiftIsAbove(Command onTrue, double heightFromGround)
    {
        super(onTrue);
        threshold = heightFromGround;
    }

    protected boolean condition()
    {
        SmartDashboard.putBoolean("IsAbove", state.liftHeightInches > threshold);
        return state.liftHeightInches > threshold;
    }
}
