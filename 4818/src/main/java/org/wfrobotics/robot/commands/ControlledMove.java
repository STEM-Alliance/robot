package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.ArmSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class ControlledMove extends Command
{
    // private final 'Subsystem' subsystem = Subsystem.getInstance();
    ArmSubsystem armSubsystem = ArmSubsystem.getInstance();
    private final IO io = IO.getInstance();

    

    public ControlledMove()
    {
        requires(armSubsystem);
    }
    protected void initialize() {
       
    }
    Double bottomA = 0.5;
    Double horizontalA = 0.5;
    Double vertA = 0.5;
    protected void execute()
    {
        vertA = vertA + 0.01 * io.getZ();
        if(vertA <= 1 && vertA>= 0) {
            armSubsystem.moveThree(vertA);
        }
        else {
            if(vertA < 0){
                vertA = 0.0;
            }
            else{
                vertA = 1.0;
            }
        }

        bottomA = bottomA + 0.01 * io.getX();
        if(bottomA <= 1 && bottomA>= 0) {
            armSubsystem.moveBottom(bottomA);
        }
        else {
            if(bottomA < 0){
                bottomA = 0.0;
            }
            else{
                bottomA = 1.0;
            }
        }
        
        horizontalA = horizontalA + 0.01 * io.getY();
        if(horizontalA <= 1 && horizontalA>= 0) {
            armSubsystem.moveTop(horizontalA);
        }
        else {
            if(horizontalA < 0){
                horizontalA = 0.0;
            }
            else{
                horizontalA = 1.0;
            }
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
