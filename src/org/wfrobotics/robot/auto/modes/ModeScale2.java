package org.wfrobotics.robot.auto.modes;

import org.wfrobotics.reuse.commands.drive.DriveOff;
import org.wfrobotics.reuse.commands.drive.DrivePath;
import org.wfrobotics.reuse.commands.drive.TurnToHeading;
import org.wfrobotics.reuse.commands.driveconfig.ResetPose;
import org.wfrobotics.reuse.commands.wait.WaitUntilAcrossX;
import org.wfrobotics.reuse.commands.wrapper.SeriesCommand;
import org.wfrobotics.reuse.config.PathContainer;
import org.wfrobotics.robot.auto.IntakeSet;
import org.wfrobotics.robot.commands.AutoLiftToBottom;
import org.wfrobotics.robot.commands.AutoLiftToScale;
import org.wfrobotics.robot.commands.lift.WaitForLiftHeight;
import org.wfrobotics.robot.commands.wrist.WristToHeight;
import org.wfrobotics.robot.paths.ScaleSecondCubeR;
import org.wfrobotics.robot.paths.StartToScaleR;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class ModeScale2 extends CommandGroup
{
    static PathContainer path = new StartToScaleR();
    static PathContainer path2 = new ScaleSecondCubeR();

    // TODO We aren't using delay commands, delete them? This also emphasizes getting used to the series command, which is good
    // TODO Think paths malfunction without recreating them. Do this in disabledInit?

    public ModeScale2()
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
            new AutoLiftToBottom(),  // Overriding to 90.0 seemed to work well with high accel turning gains
        }));
        addSequential(new DrivePath(path));

        // Reset, handle scale faults
        // TODO Detect fault on lift caught here with IfLiftIsAbove; do AutoLiftToScale, DriveOpenLoop, SignalHumanPlayer, and DriveOff?
        // addSequential(new IfLiftIsAbove(TODO));
        addSequential(new WaitForLiftHeight(20.0, false));
        // TODO Put wrist down after WaitUntilHeading to counter AutoLiftToBottom TODO above
        addSequential(new TurnToHeading(-150.0));
        addParallel(new WristToHeight(0.0));

        // Travel to second cube
        //addSequential(new DrivePath(path2));

        addSequential(new DriveOff());  // Observe robot state
    }
}
