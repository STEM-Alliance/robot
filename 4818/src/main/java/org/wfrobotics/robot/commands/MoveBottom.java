package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.ArmSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class MoveBottom extends Command
{
    // private final 'Subsystem' subsystem = Subsystem.getInstance();
    ArmSubsystem armSubsystem = ArmSubsystem.getInstance();
    private final IO io = IO.getInstance();

    Double angle = 0.0;

    public MoveBottom(Double x)
    {
        requires(armSubsystem);
        angle = x;
    }
    protected void initialize() {
        armSubsystem.moveBottom(angle);
    }
    protected void end()
    {
        armSubsystem.moveBottom(0.0);
    }
    protected boolean isFinished()
    {
        return false;
    }
}
