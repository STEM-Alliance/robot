package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class GryoEnable extends InstantCommand
{
    private boolean request;

    public GryoEnable(boolean enable)
    {
        requires(Robot.driveSubsystem);
        request = enable;
    }

    protected void initialize()
    {
        Robot.driveSubsystem.configSwerve.gyroEnable = request;
    }
}
