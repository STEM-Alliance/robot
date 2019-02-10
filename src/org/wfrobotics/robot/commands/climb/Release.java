package org.wfrobotics.robot.commands.climb;

import org.wfrobotics.robot.subsystems.Climb;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class Release extends InstantCommand
{
    Climb climb = Climb.getInstance();
    public Release()
    {
        requires(climb);
        climb.setPoppers(true);
    }

    public Release(String name)
    {
        super(name);
    }
}
