package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class IntakeSetup extends Command
{    
    boolean isOn;
    final Intake.MOTOR motor;

    public IntakeSetup(boolean on, Intake.MOTOR motor)
    {
        requires(Robot.intakeSubsystem);
        
        this.isOn = on;
        this.motor = motor;
    }

    protected void initialize() 
    {
        
    }

    protected void execute() 
    {
        double speed = (isOn) ? 1:0;
        
        Robot.intakeSubsystem.setSpeed(speed, motor);
    }

    protected boolean isFinished() 
    {
        return false;
    }

    protected void end() 
    {
        Robot.intakeSubsystem.setSpeed(0, motor);
    }

    protected void interrupted() 
    {
        end();
    }
    
    public void set(boolean on)
    {
        this.isOn = on;
    }
}
