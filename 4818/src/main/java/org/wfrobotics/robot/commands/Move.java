package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.ArmSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class Move extends Command
{
    // private final 'Subsystem' subsystem = Subsystem.getInstance();
    ArmSubsystem armSubsystem = ArmSubsystem.getInstance();
    private final IO io = IO.getInstance();

    Double angle = 0.0;

    public Move(Double x)
    {
        requires(armSubsystem);
        angle = x;
    }
    protected void initialize() {
        armSubsystem.moveToAngle(angle);
    }
    protected void end()
    {
        armSubsystem.moveToAngle(0.0);
    }
    protected boolean isFinished()
    {
        return false;
    }
}
