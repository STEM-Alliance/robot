package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeSet extends InstantCommand
{
    boolean enable;

    public IntakeSet(boolean enable)
    {
        requires(Robot.intakeSubsystem);
        this.enable = enable;
    }

    protected void initialize()
    {
        double speed = (enable) ? 1 : 0;
        SmartDashboard.putBoolean("Intake", enable);
        Robot.intakeSubsystem.setSpeed(speed);
    }
}
