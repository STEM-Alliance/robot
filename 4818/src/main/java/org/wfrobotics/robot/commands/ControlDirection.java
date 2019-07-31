package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.ArmSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class ControlDirection extends Command
{
    // private final 'Subsystem' subsystem = Subsystem.getInstance();
    ArmSubsystem armSubsystem = ArmSubsystem.getInstance();
    private final IO io = IO.getInstance();

    

    public ControlDirection()
    {
        requires(armSubsystem);
    }
    protected void initialize() {
       
    }
    Double angle = 0.0;
    protected void execute()
    {
        armSubsystem.rotateBase(angle);
        angle = angle + 0.01;
        if (angle >= 1.0){
            angle = 0.0;
        }

    }

    protected void end()
    {
        
    }
    protected boolean isFinished()
    {
        return false;
    }
}
