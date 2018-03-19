package org.wfrobotics.robot.path;

import org.wfrobotics.reuse.commands.decorator.DelayedCommand;
import org.wfrobotics.reuse.commands.decorator.SynchronizedCommand;
import org.wfrobotics.reuse.commands.drive.DriveDistance;
import org.wfrobotics.reuse.commands.drive.TurnToHeading;
import org.wfrobotics.reuse.commands.driveconfig.GyroZero;
import org.wfrobotics.robot.auto.IntakeSet;
import org.wfrobotics.robot.auto.JawsSet;
import org.wfrobotics.robot.commands.intake.SmartIntake;
import org.wfrobotics.robot.commands.lift.LiftGoHome;
import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.commands.wrist.IntakeLiftToHeight;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class ModeScale extends CommandGroup
{
    private final double inchesToSecondCube = 88;
    private final double speedOuttake = 1.0;//0.55;
    private final double timeOuttake = 0.5;
    private final double waitForGyroToFullyZero = Double.MIN_VALUE;

    public ModeScale()
    {
        addSequential(new GyroZero());  // For teleop testing
        addSequential(new WaitCommand(waitForGyroToFullyZero));
        addSequential(new SynchronizedCommand(new DriveDistance(12.0 * 24.5 - 33.6), new DelayedCommand(new LiftToScale(), 1.75)));
        addSequential(new TurnToHeading(-30.0, 1.0));
        addSequential(new IntakeSet(speedOuttake, timeOuttake, true));
        addSequential(new TurnToHeading(-145.06, 1.0)); // to find distance: x= 51 y= 73
        addSequential(new LiftToHeight(LiftHeight.Intake.get()));  // TODO ScaleChoice
        addParallel(new LiftGoHome(-0.2, 0.5));  // Ensure at smart intake height
        addParallel(new JawsSet(true, 0.1, false));  // Prime smart intake
        addParallel(new SmartIntake());
        //        addSequential(new DriveDistance(inchesToSecondCube));
        //        addSequential(new DriveDistance(12.0 * 3.0));
        nom();

        addParallel(new IntakeLiftToHeight(1.0));
        addSequential(new TurnToHeading(30.0, 1.0));  // TODO ScaleChoice
        addSequential(new SynchronizedCommand(new DriveDistance(inchesToSecondCube), new DelayedCommand(new LiftToScale(), 0.0)));
        addSequential(new TurnToHeading(-30.0, 1.0));  // TODO ScaleChoice
        addSequential(new IntakeSet(speedOuttake * 0.8, timeOuttake, true));
    }

    public void nom()
    {
        addParallel(new JawsSet(true, 0.1, false));  // Prime smart intake
        addParallel(new SmartIntake());
        addSequential(new DriveDistance(89));
    }
}