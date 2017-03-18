package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Led;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jdk.nashorn.internal.objects.annotations.Function;

/**
 * Set robot LEDs
 * This command sets the highly visible LEDs mounted on the robot
 * Useful for communication of events to driver or human player, or flaunting after we do something awesome
 */
public class LED extends Command
{
    public enum MODE { BLINK, SOLID, OFF};
    
    private MODE mode;
    
    public LED(MODE mode)
    {
        requires(Robot.ledSubsystem);
        
        this.mode = mode;
    }  
    public LED(MODE mode, double timeout)
    {
        requires(Robot.ledSubsystem);
        
        this.mode = mode;
        setTimeout(timeout);
    }
    
    @Override
    protected void initialize()
    {           
  
    }

    @Override
    protected void execute()
    {
        SmartDashboard.putString("LED Mode", mode.toString());
        
        if (mode == MODE.OFF)
        {
            Robot.ledSubsystem.fadebtwColors(0.7, 0, 1,     0, 255, 66,            0, 255, 0);
        }
        if (mode == MODE.BLINK)
        {
            Robot.ledSubsystem.blinkRed(1);
        }
        else if (mode == MODE.SOLID)
        {
            Robot.ledSubsystem.solid(0.7, 3, 0, 255, 66);
        }      
    }
    
    @Override
    protected boolean isFinished()
    {
        return isTimedOut();
    }

    @Override
    protected void end()
    {
        Robot.ledSubsystem.off();
    }

    @Override
    protected void interrupted()
    {
        end();
    }

    public void set(MODE mode)
    {
        this.mode = mode;
    }
}
