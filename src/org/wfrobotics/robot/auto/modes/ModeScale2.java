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
import org.wfrobotics.robot.commands.lift.LiftGoHome;
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

    public ModeScale2()
    {
        this.addSequential(new ResetPose(path));
        // TODO Do autozeros?

        // Travel to first scale
        this.addParallel(new SeriesCommand(new Command[] {
            new WaitUntilAcrossX(50.0),
            new WristToHeight(85.0),
            new WaitUntilAcrossX(140.0),
            new AutoLiftToScale(4.0),
            new WaitUntilAcrossX(275.0),
            new IntakeSet(0.34, 0.325),
            new AutoLiftToBottom(),  // TODO Override the angle to stay up (pass param)? Then put it down after turn
            new LiftGoHome(-0.25, 0.2),  // TODO Make finished on AtBottom, not hasZeroed? Then add to AutoLiftToBottom?
        }));
        this.addSequential(new DrivePath(path));

        // Reset, handle scale faults
        // TODO Detect fault on lift caught here with IfLiftIsAbove; do AutoLiftToScale, DriveOpenLoop, SignalHumanPlayer, and DriveOff?
        // addSequential(new IfLiftIsAbove(TODO));
        this.addSequential(new WaitForLiftHeight(30.0, false));
        // TODO Put wrist down after WaitUntilHeading to counter AutoLiftToBottom TODO above
        this.addSequential(new TurnToHeading(-150.0, 0.75));

        // Travel to second cube
        //addSequential(new DrivePath(path2));

        this.addSequential(new DriveOff());  // Observe robot state
    }
}
