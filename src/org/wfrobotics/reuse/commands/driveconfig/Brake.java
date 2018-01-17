package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class Brake extends InstantCommand
{
    private final boolean request;

    public Brake(boolean enable)
    {
        request = enable;
    }

    protected void initialize()
    {
        Robot.driveService.setBrake(request);
    }
}
