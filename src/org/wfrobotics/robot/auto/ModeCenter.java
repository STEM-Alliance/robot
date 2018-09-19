package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.AutoMode;
import org.wfrobotics.robot.commands.intake.IntakeSet;
import org.wfrobotics.robot.commands.wrist.WristToHeight;

public class ModeCenter extends AutoMode
{
    public ModeCenter()
    {
        super(null);

        // TODO Refactor

        addParallel(new WristToHeight(90.0));
        addSequential(new IntakeSet(1.0, 0.5));  // Yes, 1.0 outtake is good here
    }
}
