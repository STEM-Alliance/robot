package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.CanifierTest;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.Command;

public class CanifierBlue extends Command
{
    private final CanifierTest subsystem = CanifierTest.getInstance();

    public CanifierBlue()
    {
        requires(subsystem);
    }

    protected void initialize()
    {
        subsystem.canifier.setForAuto(Alliance.Blue);
    }

    protected void execute()
    {

    }

    protected boolean isFinished()
    {
        return false;
    }

    protected void end()
    {
        subsystem.canifier.useRobotModeColor();
    }
}