package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.commands.lift.IfLiftIsAbove;
import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.commands.lift.WaitForLiftHeight;
import org.wfrobotics.robot.commands.wrist.WristToHeight;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLiftToBottom extends CommandGroup
{
    private static final double kLiftRange = LiftHeight.Scale.get() - LiftHeight.Intake.get();
    private static final double kWristStowHeight = kLiftRange * 0.66 + LiftHeight.Intake.get();
    private static final double kWristDownHeight = kLiftRange * 0.33 + LiftHeight.Intake.get();

    public AutoLiftToBottom()
    {
        this.addSequential(new IfLiftIsAbove(new WristToHeight(90.0), kWristStowHeight));
        this.addParallel(new WristDownAfterHeight());
        // TODO Try breaking into two heights if we slam into bottom lift stops
        this.addSequential(new LiftToHeight(LiftHeight.Intake.get()));
    }

    private class WristDownAfterHeight extends CommandGroup
    {
        public WristDownAfterHeight()
        {
            this.addSequential(new WaitForLiftHeight(kWristDownHeight, false));
            this.addSequential(new WristToHeight(0.0));  // SLAM SLAM
        }
    }
}
