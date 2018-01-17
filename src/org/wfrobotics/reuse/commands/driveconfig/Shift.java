package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class Shift extends InstantCommand
{
    private final boolean request;

    public Shift(boolean highGear)
    {
        request = highGear;
    }

    protected void initialize()
    {
        Robot.driveService.setGear(request);
    }
}
