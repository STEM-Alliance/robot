package org.wfrobotics.robot.commands.climb;

import org.wfrobotics.robot.subsystems.Climb;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class Hug extends InstantCommand
{
    private final Climb climb = Climb.getInstance();
    private final boolean setGrippers;

    public Hug(boolean setGrippers)
    {
        requires(climb);
        this.setGrippers = setGrippers;
    }

    protected void initialize()
    {
        climb.setGrippers(setGrippers);
    }
}
