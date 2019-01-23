package org.wfrobotics.robot.commands;

import org.wfrobotics.reuse.commands.wrapper.SeriesCommand;
import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.commands.lift.WaitForLiftHeight;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLiftToScale extends CommandGroup
{
    private static final double kLiftRange = LiftHeight.HatchHigh.get() - LiftHeight.HatchLow.get();
    private static final double kWristUnstowHeight = kLiftRange * 0.5 + LiftHeight.HatchLow.get();

    public AutoLiftToScale()
    {
        this(0.0);
    }

    public AutoLiftToScale(double belowScaleHeight)
    {
        this(belowScaleHeight, 50.0);
    }

    public AutoLiftToScale(double belowScaleHeight, double wristAngle)
    {
        //        addSequential(new WristToHeight(80.0));  // Stow
        addParallel(new SeriesCommand(new Command[] {
            new WaitForLiftHeight(kWristUnstowHeight, true),
            //            new WristToHeight(wristAngle),  // Keep cube level to prevent slippage
        }));
        addSequential(new LiftToHeight(LiftHeight.HatchHigh.get() - belowScaleHeight));
    }
}
