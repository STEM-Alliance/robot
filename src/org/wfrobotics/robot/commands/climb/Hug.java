package org.wfrobotics.robot.commands.climb;

import org.wfrobotics.robot.subsystems.Climb;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class Hug extends InstantCommand
{
    private final Climb climb = Climb.getInstance();

    public Hug()
    {
        requires(climb);
        climb.setGrippers(true);
    }
}
