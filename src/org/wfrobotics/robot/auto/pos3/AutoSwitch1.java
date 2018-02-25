package org.wfrobotics.robot.auto.pos3;

import org.wfrobotics.reuse.commands.drivebasic.DriveDistance;
import org.wfrobotics.reuse.commands.drivebasic.TurnToHeading;
import org.wfrobotics.robot.auto.IntakeSet;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoSwitch1 extends CommandGroup
{
    public AutoSwitch1()
    {
        addSequential(new DriveDistance(135));
        addSequential(new TurnToHeading(-90, 0.2));
        addSequential(new DriveDistance(18));
        addSequential(new IntakeSet(1, 1, true));
    }

    public AutoSwitch1(String name)
    {
        super(name);
    }
}
