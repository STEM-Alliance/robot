package org.wfrobotics.robot.auto;

import org.wfrobotics.robot.commands.intake.IntakeSet;
import org.wfrobotics.robot.commands.wrist.WristToHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class ModeCenter extends CommandGroup
{
    public ModeCenter()
    {
        // TODO Refactor

        addParallel(new WristToHeight(90.0));
        addSequential(new IntakeSet(1.0, 0.5));  // Yes, 1.0 outtake is good here
    }
}
