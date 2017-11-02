package org.wfrobotics.reuse.commands.driveconfig;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class Shift extends InstantCommand
{
    private boolean request;

    public Shift(boolean highGear)
    {
        requires(Robot.driveSubsystem);
        request = highGear;
    }

    protected void initialize()
    {
        Robot.driveSubsystem.requestedHighGear = request;
    }
}
