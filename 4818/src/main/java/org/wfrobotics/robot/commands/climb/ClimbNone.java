package org.wfrobotics.robot.commands.climb;

import org.wfrobotics.robot.subsystems.Climb;

import edu.wpi.first.wpilibj.command.Command;

public class ClimbNone extends Command
{
    private final Climb climb = Climb.getInstance();

    public ClimbNone()
    {
        requires(climb);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
