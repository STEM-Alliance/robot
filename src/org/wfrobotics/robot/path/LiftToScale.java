package org.wfrobotics.robot.path;

import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.commands.wrist.IntakeLiftToHeight;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class LiftToScale extends CommandGroup
{
    public LiftToScale()
    {
        this.addParallel((new IntakeLiftToHeight(.30)));  // Keep cube level to prevent slippage
        this.addSequential(new LiftToHeight(LiftHeight.Scale.get()));
    }
}
