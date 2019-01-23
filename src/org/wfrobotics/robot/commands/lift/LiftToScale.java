package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class LiftToScale extends CommandGroup
{
    public LiftToScale()
    {
        //        this.addParallel((new WristToHeight(50.0)));  // Keep cube level to prevent slippage
        this.addSequential(new LiftToHeight(LiftHeight.HatchHigh.get()));
    }
}
