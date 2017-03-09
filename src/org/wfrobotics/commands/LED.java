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
    public double timeBlink;
    public double blinkDuration;
    
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
    public LED(HARDWARE hardware, MODE mode, double blinkDuration , double timeout)
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
//            if (timeSinceInitialized() - timeBlink < blinkDuration)
//            {
//                if(timeSinceInitialized() % 2 == 0)
//                {
//                    Robot.ledSubsystem.setOnColor(Led.HARDWARE.ALL, true, 0, 255, 0);
//                }
//                else 
//                {
//                    Robot.ledSubsystem.setOnColor(Led.HARDWARE.ALL, false, 0, 255, 0);
//                }
//            }
            Robot.ledSubsystem.blinkRed(hardware, .07);
        }
        timeBlink = timeSinceInitialized();
    }

    @Override
    protected void execute()
    {
        SmartDashboard.putString("LED Mode", mode.name());
        
        if (mode == MODE.OFF)
        {
            //Robot.ledSubsystem.setOn(hardware, false);
            Robot.ledSubsystem.flashGreen(hardware);
        }
        else if (mode == MODE.SOLID)
        {
            Robot.ledSubsystem.setOn(hardware, true);
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

    public void set(MODE mode)
    {
        this.mode = mode;
    }
}
