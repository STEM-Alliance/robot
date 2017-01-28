package org.wfrobotics.commands;

import org.wfrobotics.commands.Feed.MODE;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AugerCmd extends Command 
{    
    private boolean isOn = false;
    
    public AugerCmd(boolean isOn)
    {
        requires(Robot.augerSubsystem);
        
        this.isOn = isOn;
    }
    
    public AugerCmd(boolean isOn, double timeout)
    {
        requires(Robot.augerSubsystem);
        
        this.isOn = isOn;
        setTimeout(timeout);
    }
    
    protected void initialize()
    {
        
    }

    protected void execute()
    {
        if (isOn == true)
        {
            // speed is not 100?
            Robot.augerSubsystem.setSpeed(100);
        }
    }
    protected void end()
    {
        Robot.augerSubsystem.setSpeed(0);
    }

    protected void interrupted()
    {
        end();
    }

    @Override
    protected boolean isFinished()
    {
        // TODO Auto-generated method stub
        return false;
    }
}
