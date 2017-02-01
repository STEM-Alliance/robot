package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class IntakeSetup extends Command
{    
    boolean isRightOn;
    boolean isLeftOn;
    
    public IntakeSetup(boolean onRight, boolean onLeft)
    {
        requires(Robot.intakeSubsystem);
        
        this.isRightOn = onRight;
        this.isLeftOn = onLeft;
    }
    

    protected void initialize() 
    {
        
    }

    protected void execute() 
    {
        double speedLeft = (isLeftOn) ? 1:0;
        double speedRight= (isRightOn) ? 1:0;

        Robot.intakeSubsystem.setSpeed(speedLeft, speedRight);
    }

    protected boolean isFinished() 
    {
        return false;
    }

    protected void end() 
    {
        Robot.intakeSubsystem.setSpeed(0, 0);
    }

    protected void interrupted() 
    {
        end();
    }
    
    public void set(boolean left, boolean right)
    {
        this.isRightOn = left;
        this.isLeftOn = right;
    }
}
