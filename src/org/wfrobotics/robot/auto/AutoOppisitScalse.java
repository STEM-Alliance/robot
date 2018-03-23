package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.decorator.SynchronizedCommand;
import org.wfrobotics.reuse.commands.drive.DriveDistance;
import org.wfrobotics.reuse.commands.drive.TurnToHeading;
import org.wfrobotics.reuse.commands.driveconfig.GyroZero;
import org.wfrobotics.robot.config.Autonomous.POSITION;
import org.wfrobotics.robot.path.LiftToScale;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class AutoOppisitScalse extends CommandGroup
{
    private final double angleToScale = 50;
    //    private final double speedOuttake = 0.325;
    //    private final double timeOuttake = 0.5;
    private final double waitForGyroToFullyZero = Double.MIN_VALUE;

    public AutoOppisitScalse(POSITION location)
    {
        // Setup
        addSequential(new GyroZero());  // For teleop testing
        addSequential(new WaitCommand(waitForGyroToFullyZero));

        // Travel across field
        addSequential(new DriveDistance(12.0 * 8.5 + 2.25));
        addSequential(new DriveDistance(12.0 * 8.5 + 2.25));
        addSequential(new TurnToHeading((location == POSITION.LEFT) ? 90.0 : -90.0, 1.0));
        addSequential(new WaitCommand(.1));
        addSequential(new TurnToHeading((location == POSITION.LEFT) ? 90.0 : -90.0, 1.0));
        addSequential(new WaitCommand(.1));

        addSequential(new DriveDistance((12.0 * 8.625) + 6));
        addSequential(new DriveDistance((12.0 * 8.625) + 6));

        addSequential(new WaitCommand(.1));

        // Travel to first scale common location
        addSequential(new TurnToHeading(0, 1));
        addSequential(new WaitCommand(.2));
        addSequential(new SynchronizedCommand(new DriveDistance((12.0 * 3) - .25), new LiftToScale()));

        // -------------- Common --------------

        // Score first scale
        addSequential(new TurnToHeading((location == POSITION.RIGHT) ? angleToScale : -angleToScale, 1.0));
        addSequential(new WaitCommand(.1));
        //        addSequential(new IntakeSet(speedOuttake, timeOuttake, true));
        //
        //        // Reset
        //        addParallel(new WristToHeight(1.0));
        //        addSequential(new TurnToHeading((location == POSITION.RIGHT) ? angleToSecondCube : -angleToSecondCube, 1.0)); // to find distance: x= 51 y= 73
        //        addSequential(new WaitCommand(1.0));
        //        addParallel(new WristToHeight(-1.0));
        //        addSequential(new LiftToHeight(LiftHeight.Intake.get()));
        //
        //        // Acquire second cube
        //        addParallel(new LiftGoHome(-0.2, 0.5));  // Ensure at smart intake height
        //        addParallel(new JawsSet(true, 0.1, false));  // Prime smart intake
        //        addParallel(new SmartIntake());
        //        addSequential(new DriveInfared(6, 89.0 + 3, 1.5, 3));
        //        addSequential(new WaitCommand(1.0));  // Unique to opposite auto routine
        //
        //        // Travel to second scale
        //        addParallel(new WristToHeight(1.0));
        //        addSequential(new SynchronizedCommand(new DriveDistance(-inchesToSecondCube), new LiftToHeight(LiftHeight.Scale.get())));
        //
        //        // Score second cube
        //        addSequential(new SynchronizedCommand(new TurnToHeading((location == POSITION.RIGHT) ? angleToScale : -angleToScale, 1.0), new LiftToScale()));
        //        addSequential(new WaitCommand(.1));
        //        addSequential(new IntakeSet(speedOuttake, timeOuttake, true));
        //
        //        // Reset
        //        addSequential(new TurnToHeading((location == POSITION.RIGHT) ? -20 : 20, 1.0));
        //        addSequential(new LiftToHeight(LiftHeight.Intake.get()));
    }
}

