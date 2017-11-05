package org.wfrobotics.robot.auto;

import org.wfrobotics.robot.commands.Conveyor;
import org.wfrobotics.robot.commands.IntakeSet;
import org.wfrobotics.robot.commands.RevOff;
import org.wfrobotics.robot.commands.Shoot;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutonomousShoot extends CommandGroup
{
    public AutonomousShoot(Conveyor.MODE feedMode, double speedFeed, double speedUnjam, double timeout)
    {
        addSequential(new IntakeSet(true));
        addSequential(new Shoot(feedMode, speedFeed, speedUnjam, timeout));
        addSequential(new IntakeSet(false));
        addSequential(new RevOff());
    }
}
