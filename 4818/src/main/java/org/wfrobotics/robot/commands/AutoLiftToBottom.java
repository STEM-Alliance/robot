package org.wfrobotics.robot.commands;

import org.wfrobotics.reuse.commands.wrapper.SeriesCommand;
import org.wfrobotics.robot.commands.lift.IfLiftIsAbove;
import org.wfrobotics.robot.commands.lift.LiftGoHome;
import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.commands.lift.WaitForLiftHeight;
import org.wfrobotics.robot.commands.wrist.WristToHeight;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLiftToBottom extends CommandGroup
{
    private static final double kLiftRange = LiftHeight.Scale.get() - LiftHeight.Intake.get();
    private static final double kWristStowHeight = kLiftRange * 0.66 + LiftHeight.Intake.get();
    private static final double kWristUnstowHeight = kLiftRange * 0.66 + LiftHeight.Intake.get();

    public AutoLiftToBottom()
    {
        this(0.0);
    }

    public AutoLiftToBottom(double overrideWristDownAngle)
    {
        addSequential(new IfLiftIsAbove(new WristToHeight(90.0), kWristStowHeight));
        addParallel(new SeriesCommand(new Command[] {
            new WaitForLiftHeight(kWristUnstowHeight, false),
            new WristToHeight(overrideWristDownAngle),  // SLAM SLAM
        }));
        addSequential(new LiftToHeight(LiftHeight.Intake.get() + 4.0));  // Gentler slamming
        addSequential(new LiftToHeight(LiftHeight.Intake.get()));
        addSequential(new LiftGoHome(-0.25, 2.0));  // Anything the command didn't let the PID finish
    }
}
