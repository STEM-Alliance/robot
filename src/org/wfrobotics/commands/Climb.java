package org.wfrobotics.commands;

import org.wfrobotics.commands.Up.MODE;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class Climb extends CommandGroup 
{
    public Climb()
    {
        addSequential(new Up(MODE.CLIMB));
        // TODO Flaunt. Flash lights when we are at the top of the rope.
        addSequential(new Up(MODE.HOLD));
        // TODO DRL command to brake for after the robot is disable at the end of the match
    }
}
