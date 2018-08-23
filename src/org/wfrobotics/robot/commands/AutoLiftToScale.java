package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.commands.wrist.WristToHeight;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLiftToScale extends CommandGroup
{
    public AutoLiftToScale()
    {

        this.addParallel((new WristToHeight(50.0)));  // Keep cube level to prevent slippage
        this.addSequential(new LiftToHeight(LiftHeight.Scale.get()));
        // TODO Delay level out wrist once up with Command that waits for state?
    }
}
