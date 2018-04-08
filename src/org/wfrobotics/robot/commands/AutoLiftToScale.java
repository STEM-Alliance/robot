package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.commands.wrist.WristToHeight;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLiftToScale extends CommandGroup
{
    public AutoLiftToScale()
    {
        this.addParallel((new WristToHeight(.55)));  // Keep cube level to prevent slippage
        this.addSequential(new LiftToHeight(LiftHeight.Scale.get()));
    }
}
