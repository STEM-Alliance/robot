package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class IntakeSetup extends Command
{
    boolean onDesired;
    
    public IntakeSetup(boolean on)
    {
        requires(Robot.intakeSubsystem);
        
        this.onDesired = on;
    }
    
    protected void initialize() 
    {
        
    }

    protected void execute() 
    {
        double speed = (onDesired) ? 1 : 0;

        Robot.intakeSubsystem.setSpeed(speed);
    }

    protected boolean isFinished() 
    {
        return false;
    }

    protected void end() 
    {
        Robot.intakeSubsystem.setSpeed(0);
    }

    protected void interrupted() 
    {
        end();
    }
    
    public void set(boolean isOn)
    {
        this.onDesired = isOn;
    }
}
