package org.wfrobotics.commands;

import org.wfrobotics.commands.Up.MODE;
import org.wfrobotics.subsystems.Led;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class Climb extends CommandGroup 
{
    public Climb()
    {
        addSequential(new Up(MODE.CLIMB));
//        addParallel(new LED(Led.HARDWARE.ALL, LED.MODE.BLINK));
        addSequential(new Up(MODE.HOLD));
        // TODO DRL command to brake for after the robot is disable at the end of the match
    }
}
