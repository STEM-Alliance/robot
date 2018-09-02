package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.commands.wrist.WristToHeight;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLiftToScale extends CommandGroup
{
    public AutoLiftToScale()
    {
        this(0.0);
    }

    public AutoLiftToScale(double belowScaleHeight)
    {
        // TODO Wrist more vertical during the middle of the motion
        this.addParallel((new WristToHeight(50.0)));  // Keep cube level to prevent slippage
        this.addSequential(new LiftToHeight(LiftHeight.Scale.get() - belowScaleHeight));
    }
}
