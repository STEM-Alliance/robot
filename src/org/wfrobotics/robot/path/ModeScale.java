package org.wfrobotics.robot.path;

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
import org.wfrobotics.robot.config.Autonomous.POSITION;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class ModeScale extends CommandGroup
{
    private final double angleToScale = -50; //-50
    private final double angleToSecondCube = -151.89; //-145.06
    private final double inchesToSecondCube = 72; //78
    private final double speedOuttake = 1.0;
    private final double timeOuttake = 0.5;
    private final double waitForGyroToFullyZero = Double.MIN_VALUE;

    public ModeScale(POSITION location)
    {
        // Setup
        addSequential(new GyroZero());  // For teleop testing
        addSequential(new WaitCommand(waitForGyroToFullyZero));

        // Travel to first scale
        addSequential(new DriveDistance(12.0 * 24.5 - 33.6));
        //        addSequential(new TurnToHeading(0, 1.0));
        //        addSequential(new WaitCommand(.2));
        //        addSequential(new SynchronizedCommand(new DriveDistance(12.0 * 12.0), new LiftToScale()));

        // Score first scale
        addSequential(new TurnToHeading((location == POSITION.RIGHT) ? angleToScale : -angleToScale, 1.0));
        addSequential(new IntakeSet(speedOuttake, timeOuttake, true));

        // Reset
        addSequential(new TurnToHeading((location == POSITION.RIGHT) ? angleToSecondCube : -angleToSecondCube, 1.0)); // to find distance: x= 51 y= 73
        addSequential(new WaitCommand(0.3));
        addSequential(new LiftToHeight(LiftHeight.Intake.get()));

        // Acquire second cube
        addParallel(new LiftGoHome(-0.2, 0.5));  // Ensure at smart intake height
        addParallel(new JawsSet(true, 0.1, false));  // Prime smart intake
        addParallel(new IntakeLiftToHeight(0.0));
        addParallel(new SmartIntake());
        addSequential(new DriveInfared(6, 1.5));
        addSequential(new WaitCommand(.3));

        // Travel to second scale
        addParallel(new IntakeLiftToHeight(1.0));
        addSequential(new SynchronizedCommand(new DriveDistance(-inchesToSecondCube), new LiftToHeight(LiftHeight.Scale.get())));

        // Score second cube
        addSequential(new SynchronizedCommand(new TurnToHeading((location == POSITION.RIGHT) ? angleToScale : -angleToScale, 1.0), new LiftToScale()));
        addSequential(new IntakeSet(speedOuttake, timeOuttake, true));

        // Reset
        addSequential(new TurnToHeading((location == POSITION.RIGHT) ? 20 : -20, 1.0));
        addSequential(new LiftToHeight(LiftHeight.Intake.get()));
    }
}