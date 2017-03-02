package org.wfrobotics.commands;

import org.wfrobotics.subsystems.Led;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class Climb extends CommandGroup 
{
    public enum MODE {CLIMB, OFF};

    public Climb(MODE mode)
    {
        if (mode == MODE.OFF)
        {
            addParallel(new LED(Led.HARDWARE.ALL, LED.MODE.BLINK));
            addSequential(new Up(Up.MODE.VARIABLE_SPEED));
        }
        else if (mode == MODE.CLIMB)
        {
            addParallel(new LED(Led.HARDWARE.ALL, LED.MODE.BLINK));
            addSequential(new Up(Up.MODE.CLIMB));
        } 
    }
}
