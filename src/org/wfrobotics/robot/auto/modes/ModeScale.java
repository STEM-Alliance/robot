package org.wfrobotics.robot.auto.modes;

import org.wfrobotics.reuse.commands.drive.DriveOff;
import org.wfrobotics.reuse.commands.drive.DrivePath;
import org.wfrobotics.reuse.commands.drive.TurnToHeading;
import org.wfrobotics.reuse.commands.driveconfig.ResetPose;
import org.wfrobotics.reuse.commands.wait.WaitUntilAcrossX;
import org.wfrobotics.reuse.commands.wrapper.SeriesCommand;
import org.wfrobotics.reuse.commands.wrapper.SynchronizedCommand;
import org.wfrobotics.reuse.config.PathContainer;
import org.wfrobotics.robot.auto.IntakeSet;
import org.wfrobotics.robot.commands.AutoLiftToBottom;
import org.wfrobotics.robot.commands.AutoLiftToScale;
import org.wfrobotics.robot.commands.intake.SmartIntake;
import org.wfrobotics.robot.commands.lift.WaitForLiftHeight;
import org.wfrobotics.robot.commands.wrist.WristToHeight;
import org.wfrobotics.robot.paths.ScaleSecondCubeR;
import org.wfrobotics.robot.paths.SecondCubeToScaleR;
import org.wfrobotics.robot.paths.StartToScaleR;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class ModeScale extends CommandGroup
{
    static PathContainer path = new StartToScaleR();
    static PathContainer path2 = new ScaleSecondCubeR();
    static PathContainer path3 = new SecondCubeToScaleR();

    // TODO Think paths malfunction without recreating them. Do this in disabledInit?
    // TODO Create AutoModeBase from CommandGroup, recalc paths on auto select?

    public ModeScale()
    {
        addSequential(new ResetPose(path));
        // TODO Do autozeros?

        // Travel to first scale
        addParallel(new SeriesCommand(new Command[] {
            new WaitUntilAcrossX(50.0),
            new WristToHeight(85.0),
            new WaitUntilAcrossX(140.0),
            new AutoLiftToScale(4.0),
            new WaitUntilAcrossX(275.0),
            new IntakeSet(0.34, 0.325),
            new AutoLiftToBottom(),
        }));
        addSequential(new DrivePath(path));

        // Reset
        addSequential(new WaitForLiftHeight(20.0, false));
        addSequential(new TurnToHeading(-150.0));

        // Travel to second cube
        addParallel(new WristToHeight(0.0));
        addParallel(new SmartIntake());
        addSequential(new DrivePath(path2));

        // Input second cube into scale
        addParallel(new WristToHeight(85.0));  // TODO Does this fix oscillation?
        addSequential(new TurnToHeading(0.0));  // TODO Start lifting with a WaitForHeading?
        addSequential(new SynchronizedCommand(new DrivePath(path3), new AutoLiftToScale(4.0, 30.0)));
        addSequential(new IntakeSet(0.34, 0.325));

        // Reset
        addSequential(new AutoLiftToBottom());
        addSequential(new WaitForLiftHeight(20.0, false));
        addSequential(new TurnToHeading(-150.0));

        // Travel to third cube
        // TODO Drive a bit?

        addSequential(new DriveOff());  // Observe robot state
    }
}
