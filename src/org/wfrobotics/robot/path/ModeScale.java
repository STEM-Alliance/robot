package org.wfrobotics.robot.path;

import org.wfrobotics.reuse.commands.decorator.DelayedCommand;
import org.wfrobotics.reuse.commands.decorator.SynchronizedCommand;
import org.wfrobotics.reuse.commands.drive.DriveDistance;
import org.wfrobotics.reuse.commands.drive.DrivePath;
import org.wfrobotics.reuse.commands.drive.TurnToHeading;
import org.wfrobotics.reuse.commands.driveconfig.GyroZero;
import org.wfrobotics.robot.auto.IntakeSet;
import org.wfrobotics.robot.auto.JawsSet;
import org.wfrobotics.robot.commands.AutoLiftToBottom;
import org.wfrobotics.robot.commands.intake.SmartIntake;
import org.wfrobotics.robot.commands.lift.LiftGoHome;
import org.wfrobotics.robot.commands.wrist.IntakeLiftToHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class ModeScale extends CommandGroup
{
    private final double inchesToSecondCube = 12.0 * 4.0;
    private final double speedOuttake = 1.0;//0.55;
    private final double timeOuttake = 0.5;
    private final double waitForGyroToFullyZero = Double.MIN_VALUE;

    public ModeScale()
    {
        addSequential(new GyroZero());  // For teleop testing
        addSequential(new WaitCommand(waitForGyroToFullyZero));
        addSequential(new SynchronizedCommand(new DrivePath("Scale"), new DelayedCommand(new LiftToScale(), 3.0)));
        addSequential(new IntakeSet(speedOuttake, timeOuttake, true));
        addParallel(new AutoLiftToBottom());  // TODO ScaleChoice
        addSequential(new TurnToHeading(-175.0, 1.0));
        addParallel(new LiftGoHome(-0.2, 0.5));  // Ensure at smart intake height
        addParallel(new JawsSet(true, 0.1, false));  // Prime smart intake
        addParallel(new SmartIntake());
        //        addSequential(new DriveDistance(inchesToSecondCube));
        //        addSequential(new DriveDistance(12.0 * 3.0));
        nom();
        addParallel(new IntakeLiftToHeight(1.0));
        addSequential(new TurnToHeading(0.0, 1.0));  // TODO ScaleChoice
        addSequential(new SynchronizedCommand(new DriveDistance(inchesToSecondCube), new DelayedCommand(new LiftToScale(), 0.0)));
        addSequential(new TurnToHeading(-20.0, 1.0));  // TODO ScaleChoice
        addSequential(new IntakeSet(speedOuttake * 0.8, timeOuttake, true));
    }

    public void nom()
    {
        addParallel(new JawsSet(true, 0.1, false));  // Prime smart intake
        addParallel(new SmartIntake());
        addSequential(new DriveInfared(6.0, 1.5));
    }
}