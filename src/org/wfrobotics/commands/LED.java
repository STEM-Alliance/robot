package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Led.HARDWARE;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Set robot LEDs
 * This command sets the highly visible LEDs mounted on the robot
 * Useful for communication of events to driver or human player, or flaunting after we do something awesome
 */
public class LED extends Command
{
    public enum MODE { BLINK, SOLID, OFF};
    
    private final HARDWARE hardware;
    private MODE mode;
    
    public LED(HARDWARE hardware, MODE mode)
    {
        requires(Robot.ledSubsystem);
        
        this.hardware = hardware;
        this.mode = mode;

    }
    
    public LED(HARDWARE hardware, MODE mode, double timeout)
    {
        requires(Robot.ledSubsystem);
        
        this.hardware = hardware;
        this.mode = mode;
        setTimeout(timeout);
    }
    
    @Override
    protected void initialize()
    {
        if (mode == MODE.BLINK)
        {
            Robot.ledSubsystem.blink(hardware, .7);
        }
    }

    @Override
    protected void execute()
    {   
        SmartDashboard.putString("LED Mode", mode.name());
        if (mode == MODE.OFF)
        {
            Robot.ledSubsystem.setOn(hardware, false);
        }
        else if (mode == MODE.SOLID)
        {
            Robot.ledSubsystem.setOn(hardware, true);
        }      
    }
    
    @Override
    protected boolean isFinished()
    {
        return false;//isTimedOut();  // TODO delete this comment or remove timeout from constructor
    }

    @Override
    protected void end()
    {
        Robot.ledSubsystem.setOn(hardware, false);
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
