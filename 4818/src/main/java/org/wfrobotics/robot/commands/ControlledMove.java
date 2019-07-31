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
    Double baseAngle = 0.5;
    Double reachAngle = 0.5;
    Double liftAngle = 0.5;
    Double clawAngle = 0.5;
    protected void execute()
    {
        clawAngle = clawAngle + 0.005 * io.getClaw();
        if(clawAngle <= 1 && clawAngle>= 0) {
            armSubsystem.pinch(clawAngle);
        }
        else {
            if(clawAngle < 0){
                clawAngle = 0.0;
            }
            else{
                clawAngle = 1.0;
            }
        }


        liftAngle = liftAngle + 0.01 * io.getLift();
        if(liftAngle <= 1 && liftAngle>= 0) {
            armSubsystem.lift(liftAngle);
        }
        else {
            if(liftAngle < 0){
                liftAngle = 0.0;
            }
            else{
                liftAngle = 1.0;
            }
        }

        baseAngle = baseAngle + 0.01 * io.getBase();
        if(baseAngle <= 1 && baseAngle>= 0) {
            armSubsystem.rotateBase(baseAngle);
        }
        else {
            if(baseAngle < 0){
                baseAngle = 0.0;
            }
            else{
                baseAngle = 1.0;
            }
        }
        
        reachAngle = reachAngle + 0.01 * io.getReach();
        if(reachAngle <= 1 && reachAngle>= 0) {
            armSubsystem.reach(reachAngle);
        }
        else {
            if(reachAngle < 0){
                reachAngle = 0.0;
            }
            else{
                reachAngle = 1.0;
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
