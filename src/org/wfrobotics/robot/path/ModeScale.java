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
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class ModeScale extends CommandGroup
{
    private final double angleToScale = -50; //-50
    private final double inchesToSecondCube = 72; //78
    private final double angleToSecondCube = -151.89; //-145.06
    private final double speedOuttake = 1.0;//0.55;
    private final double timeOuttake = 0.5;
    private final double waitForGyroToFullyZero = Double.MIN_VALUE;

    public ModeScale()
    {
        addSequential(new GyroZero());  // For teleop testing
        addSequential(new WaitCommand(waitForGyroToFullyZero));
        addSequential(new DriveDistance(12.0 * 12.5 - 33.6));
        addSequential(new TurnToHeading(0, 1.0));
        addSequential(new SynchronizedCommand(new DriveDistance(12.0 * 12.0), new LiftToScale()));
        addSequential(new TurnToHeading(angleToScale, 1.0));
        addSequential(new IntakeSet(speedOuttake, timeOuttake, true));
        addSequential(new TurnToHeading(angleToSecondCube, 1)); // to find distance: x= 51 y= 73
        addSequential(new WaitCommand(0.3));
        addSequential(new LiftToHeight(LiftHeight.Intake.get()));  // TODO ScaleChoice
        addParallel(new LiftGoHome(-0.2, 0.5));  // Ensure at smart intake height
        addParallel(new JawsSet(true, 0.1, false));  // Prime smart intake
        addParallel(new SmartIntake());
        //        addSequential(new DriveDistance(inchesToSecondCube));
        //        addSequential(new DriveDistance(12.0 * 3.0));
        nom();

        addParallel(new IntakeLiftToHeight(1.0));
        //        addSequential(new TurnToHeading(30.0, 1.0));  // TODO ScaleChoice
        addSequential(new SynchronizedCommand(new DriveDistance(-inchesToSecondCube), new LiftToHeight(LiftHeight.Scale.get())));
        addSequential(new SynchronizedCommand(new TurnToHeading(angleToScale, 1.0), new LiftToScale()));  // TODO ScaleChoice
        addSequential(new IntakeSet(speedOuttake, timeOuttake, true));
        addSequential(new TurnToHeading(20, 1.0));
    }

    public void nom()
    {
        addParallel(new JawsSet(true, 0.1, false));  // Prime smart intake
        addParallel(new SmartIntake());
        addSequential(new DriveInfared(6, 1.5));
        //        addSequential(new DriveDistance(inchesToSecondCube));
        //        addSequential (new DriveDistance(-10));
    }
}