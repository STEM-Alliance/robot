package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.TurnToTarget;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class EatCube extends CommandGroup
{
    public EatCube()
    {
        this.addSequential(new TurnToTarget(2.0));
        this.addSequential(new DriveInfared(10, 60, 3, 3));
    }
}
