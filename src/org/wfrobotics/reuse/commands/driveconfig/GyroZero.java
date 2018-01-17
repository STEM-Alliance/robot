package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class GyroZero extends InstantCommand
{
    public GyroZero()
    {
        requires(Robot.driveService.getSubsystem());
    }

    protected void initialize()
    {
        Robot.driveService.zeroGyro();
    }
}
