package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.CanifierTest;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.Command;

public class CanifierRed extends Command
{
    private final CanifierTest subsystem = CanifierTest.getInstance();

    public CanifierRed()
    {
        requires(subsystem);
    }

    protected void initialize()
    {
        subsystem.canifier.setForAuto(Alliance.Red);
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