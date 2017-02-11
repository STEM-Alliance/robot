package org.wfrobotics.commands;

import org.wfrobotics.commands.Up.MODE;
import org.wfrobotics.subsystems.Led;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class Climb extends CommandGroup 
{
    private Up.MODE mode;
    
    public Climb(Up.MODE mode)
    {
        this.mode = mode;
        addSequential(new Up(MODE.CLIMB));
        addParallel(new LED(Led.HARDWARE.ALL, LED.MODE.BLINK));
        addSequential(new Up(MODE.OFF));
        // TODO DRL command to brake for after the robot is disable at the end of the match
    }
    
    protected void execute()
    {
        if (mode == Up.MODE.OFF)
        {
            
        }
        else if (mode == Up.MODE.CLIMB)
        {
            
        }
        else
        {
            
        }
    }
    
    public void set(Up.MODE mode)
    {
        this.mode = mode;
    }
}
