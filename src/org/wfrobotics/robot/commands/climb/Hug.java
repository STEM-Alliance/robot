package org.wfrobotics.robot.commands.climb;

import org.wfrobotics.robot.subsystems.Climb;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class Hug extends InstantCommand
{
    Climb climb = Climb.getInstance();
    public Hug()
    {
        requires(climb);
        climb.setPoppers(true);
    }

    public Hug(String name)
    {
        super(name);
    }
}
