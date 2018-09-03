package org.wfrobotics.robot.auto.modes;

import java.util.function.DoubleSupplier;

import org.wfrobotics.reuse.commands.drive.BufferDistanceToObject;
import org.wfrobotics.reuse.commands.drive.DriveDistance;
import org.wfrobotics.reuse.commands.drive.TurnToHeading;
import org.wfrobotics.reuse.commands.driveconfig.GyroZero;
import org.wfrobotics.reuse.commands.wrapper.DelayedCommand;
import org.wfrobotics.reuse.commands.wrapper.SynchronizedCommand;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.auto.IntakeSet;
import org.wfrobotics.robot.auto.JawsSet;
import org.wfrobotics.robot.auto.LiftToScale;
import org.wfrobotics.robot.commands.intake.SmartIntake;
import org.wfrobotics.robot.commands.lift.LiftGoHome;
import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.commands.wrist.WristToHeight;
import org.wfrobotics.robot.config.Autonomous.POSITION;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class ModeScale extends CommandGroup
{
    private final DoubleSupplier intakeInfared = () -> RobotState.getInstance().getIntakeDistanceCM();
    private final double angleToScale = -50; //-50
    private final double angleToSecondCube = -145.0; //-145.06
    private final double inchesToSecondCube = 72; //78
    private final double speedOuttake = 0.35;
    private final double timeOuttake = 0.5;
    private final double waitForGyroToFullyZero = Double.MIN_VALUE;

    public ModeScale(POSITION location)
    {
        // Setup
        addSequential(new GyroZero());  // For teleop testing
        addSequential(new WaitCommand(waitForGyroToFullyZero));

        // Travel to first scale
        addSequential(new DriveDistance(12.0 * 12.5 - 29.1));
        addSequential(new SynchronizedCommand(new Command[] {
            new DriveDistance(12.0 * 12.0),
            new LiftToScale(),
        }));

        // -------------- Common --------------

        // Score first scale
        addSequential(new TurnToHeading((location == POSITION.RIGHT) ? angleToScale : -angleToScale));
        addSequential(new WaitCommand(.1));
        addSequential(new IntakeSet(speedOuttake, timeOuttake));

        // Reset
        addParallel(new WristToHeight(90.0));
        addSequential(new DelayedCommand(new TurnToHeading((location == POSITION.RIGHT) ? angleToSecondCube : -angleToSecondCube), 1.0)); // to find distance: x= 51 y= 73
        addSequential(new WaitCommand(0.4));
        addParallel(new WristToHeight(0.0));
        addSequential(new LiftToHeight(LiftHeight.Intake.get()));

        // Acquire second cube
        addParallel(new LiftGoHome(-0.2, 0.5));  // Ensure at smart intake height
        addParallel(new JawsSet(true, 0.1, false));  // Prime smart intake
        addParallel(new SmartIntake());
        addSequential(new BufferDistanceToObject(intakeInfared, 10.0, 89.0 + 4.5, 1.5, 3.0));  // DriveInfared
        addSequential(new WaitCommand(.1));

        // Travel to second scale
        addParallel(new WristToHeight(90.0));
        addSequential(new SynchronizedCommand(new Command[] {
            new DriveDistance(-inchesToSecondCube),
            new DelayedCommand(new LiftToHeight(LiftHeight.Scale.get()), 0.05)
        }));

        // Score second cube
        addSequential(new SynchronizedCommand(new Command[] {
            new TurnToHeading((location == POSITION.RIGHT) ? angleToScale : -angleToScale),
            new LiftToScale(),
        }));
        addSequential(new WaitCommand(.1));
        addSequential(new IntakeSet(speedOuttake, timeOuttake));

        // Reset
        addSequential(new TurnToHeading((location == POSITION.RIGHT) ? 20 : -20));
        addSequential(new LiftToHeight(LiftHeight.Intake.get()));
    }
}