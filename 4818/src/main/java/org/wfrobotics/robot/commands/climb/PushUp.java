package org.wfrobotics.robot.commands.climb;

import org.wfrobotics.robot.subsystems.Climb;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class  PushUp extends InstantCommand
{
    private final Climb climb = Climb.getInstance();
    private final boolean setPushUp;

    public PushUp(boolean setPushUp)
    {
        requires(climb);
        this.setPushUp = setPushUp;
    }

    protected void initialize()
    {
        climb.setPushUp(setPushUp);
    }
}
