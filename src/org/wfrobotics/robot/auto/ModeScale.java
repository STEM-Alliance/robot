package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.config.ResetPose;
import org.wfrobotics.reuse.commands.drive.DrivePath;
import org.wfrobotics.reuse.commands.drive.TurnToHeading;
import org.wfrobotics.reuse.commands.wait.WaitUntilAcrossX;
import org.wfrobotics.reuse.commands.wrapper.AutoMode;
import org.wfrobotics.reuse.commands.wrapper.SeriesCommand;
import org.wfrobotics.reuse.commands.wrapper.SynchronizedCommand;
import org.wfrobotics.reuse.config.PathContainer;
import org.wfrobotics.robot.commands.AutoLiftToBottom;
import org.wfrobotics.robot.commands.AutoLiftToScale;
import org.wfrobotics.robot.commands.intake.IntakeSet;
import org.wfrobotics.robot.commands.intake.SmartIntake;
import org.wfrobotics.robot.commands.lift.WaitForLiftHeight;
import org.wfrobotics.robot.commands.wrist.WristToHeight;
import org.wfrobotics.robot.paths.ScaleSecondCubeR;
import org.wfrobotics.robot.paths.SecondCubeToScaleR;
import org.wfrobotics.robot.paths.StartToScaleR;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class ModeScale extends AutoMode
{
    private static final double kIntakeShootPower = 0.34;
    private static final double kIntakeShootTimeout = 0.325;
    private static final double kLiftHeightStartTurn = 20.0;
    private static final double kWristStowDegrees = 87.5;

    public ModeScale()
    {
        final PathContainer path = new StartToScaleR();
        final PathContainer path2 = new ScaleSecondCubeR();
        final PathContainer path3 = new SecondCubeToScaleR();

        final double kPath2StartDegrees = path2.getStartPose().getRotation().getDegrees();
        final double kPath3StartDegrees = path3.getStartPose().getRotation().getDegrees();

        // Reset
        addParallel(new ResetPose(path));
        addSequential(new WaitCommand(startingDelay));

        // Travel to first scale
        addParallel(new SeriesCommand(new Command[] {
            new WaitUntilAcrossX(50.0),
            new WristToHeight(kWristStowDegrees),
            new WaitUntilAcrossX(140.0),
            new AutoLiftToScale(4.0),
            new WaitUntilAcrossX(275.0),
            new IntakeSet(kIntakeShootPower, kIntakeShootTimeout),
            new AutoLiftToBottom(),
        }));
        addSequential(new DrivePath(path));

        // Reset
        addSequential(new WaitForLiftHeight(kLiftHeightStartTurn, false));
        addSequential(new TurnToHeading(kPath2StartDegrees));

        // Travel to second cube
        addParallel(new WristToHeight(0.0));
        addParallel(new SmartIntake());
        addSequential(new DrivePath(path2));

        // Input second cube into scale
        addParallel(new WristToHeight(kWristStowDegrees));  // TODO Does this fix oscillation?
        // TODO Start lifting with a WaitForHeading?
        addSequential(new TurnToHeading(kPath3StartDegrees));
        addSequential(new SynchronizedCommand(new DrivePath(path3), new AutoLiftToScale(4.0, 30.0)));
        addSequential(new IntakeSet(kIntakeShootPower, kIntakeShootTimeout));

        // Reset
        addSequential(new AutoLiftToBottom());
        addSequential(new WaitForLiftHeight(kLiftHeightStartTurn, false));
        addSequential(new TurnToHeading(-150.0));

        // Travel to third cube
        // TODO Drive a bit?
    }
}
