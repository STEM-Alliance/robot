package org.wfrobotics.robot.commands.drive;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DoubleVision extends CommandGroup
{
    public DoubleVision()
    {
        this.addSequential(new TurnToTarget());
        this.addSequential(new TurnToTarget());
    }
}
