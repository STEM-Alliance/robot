package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class Brake extends InstantCommand
{
    private boolean request;

    public Brake(boolean enable)
    {
        requires(Robot.driveService.getSubsystem());
        request = enable;
    }

    protected void initialize()
    {
        Robot.driveService.setBrake(request);
    }
}
