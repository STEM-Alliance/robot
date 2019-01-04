package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.wrapper.AutoMode;

public class ModeCenter extends AutoMode
{
    public ModeCenter()
    {
        //        addSequential(new WaitCommand(startingDelay));
        //
        //        // TODO Refactor
        //
        //        addParallel(new WristToHeight(90.0));
        //        addSequential(new IntakeSet(1.0, 0.5));  // Yes, 1.0 outtake is good here
    }
}
