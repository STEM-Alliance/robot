package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class ShiftToggle extends InstantCommand
{
    public ShiftToggle()
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize()
    {
        Robot.driveSubsystem.requestedHighGear = !RobotState.getInstance().robotGear;
    }
}
