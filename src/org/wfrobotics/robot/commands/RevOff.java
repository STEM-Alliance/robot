package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.Commands;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class RevOff extends InstantCommand
{
    public RevOff()
    {
        requires(Robot.shooterSubsystem);
    }

    protected void initialize()
    {
        Robot.shooterSubsystem.topThenBottom(0,  Commands.SHOOTER_READY_SHOOT_SPEED_TOLERANCE_RPM);
    }
}
