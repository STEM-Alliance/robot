package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.commands.lift.LiftToHeightAndHold;
import org.wfrobotics.robot.commands.wrist.IntakeLiftToHeight;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLiftToScale extends CommandGroup
{
    public AutoLiftToScale()
    {
        this.addParallel((new IntakeLiftToHeight(.55)));  // Keep cube level to prevent slippage
        this.addSequential(new LiftToHeightAndHold(LiftHeight.Scale.get()));
    }
}
