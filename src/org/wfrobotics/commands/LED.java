package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Led.HARDWARE;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Set robot LEDs
 * This command sets the highly visible LEDs mounted on the robot
 * Useful for communication of events to driver or human player, or flaunting after we do something awesome
 * @author drlindne
 *
 */
public class LED extends Command
{
    public enum MODE { BLINK, SOLID, OFF};
    
    private final HARDWARE hardware;
    private final MODE mode;
    public boolean isOn;
    
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
        
    }

    @Override
    protected void execute()
    {
        if (mode == MODE.OFF || mode == MODE.SOLID)
        {
            Robot.ledSubsystem.setOn(hardware, isOn);
        }
        else if (mode == MODE.BLINK)
        {
            double time = Math.floor(timeSinceInitialized());
            boolean state = time % 2 == 0;

            Robot.ledSubsystem.setOn(hardware, state);
        }
        else
        {
            DriverStation.reportError("LED mode not supported", true);
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
        Robot.ledSubsystem.setOn(hardware, false);
    }

    @Override
    protected void interrupted()
    {
        end();
    }
}
