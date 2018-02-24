package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.reuse.utilities.Utilities;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Pull in the cube purely based on sensors */
public class SmartIntake extends CommandGroup
{
    private final double kCubeIn = 5.0;
    private final double kCubeInDeadband = 0.25;

    protected final RobotState state = RobotState.getInstance();
    protected final IntakeSubsystem intake = Robot.intakeSubsystem;

    /**
     * This command gets the distance from the subsystem and based on that distance
     * drive the motors at different speeds
     */
    public SmartIntake()
    {
        requires(intake);
    }

    protected void initialize()
    {
        SmartDashboard.putString("Intake", "Smart");
    }

    protected void execute()
    {
        if (state.liftHeightInches < .5 && !intake.getVertical())
        {
            double distanceToCube = state.intakeDistance;

            autoIntake(distanceToCube);
            autoJaws(distanceToCube);
            autoWrist(distanceToCube);
        }
        else  // Cancel intaking if transition to lifting
        {
            intake.setMotor(0);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }

    private void autoIntake(double distanceToCube)
    {
        double speed = 0.0;

        if (distanceToCube < kCubeIn && distanceToCube > kCubeInDeadband)
        {
            speed = -Utilities.scaleToRange(distanceToCube, kCubeInDeadband, kCubeIn, .25, 1);
        }
        else if (distanceToCube > kCubeIn && distanceToCube < 50)  // TODO Need to move sensor, otherwise we stall motors
        {
            speed = -1.0;
        }

        // TODO After it's in for a little bit, it's SUPER effective to pulse the cube out a sec then back in to orient it
        //      This would also help us not stall if we don't drive wheels after that pulse. Or we move the distance sensor back enough to always be in a valid range.

        intake.setMotor(speed);
    }

    private void autoJaws(double distanceToCube)
    {
        String isJawtomated = "No";

        if (distanceToCube < 30)
        {
            intake.setHorizontal(false);  // Can't always set, otherwise we chatter?
            isJawtomated = "Close";
        }
        else if (distanceToCube > 30 && distanceToCube < 60)  // TODO find ideal range to be auto-opened, put in RobotMap or use robot state
        {
            intake.setHorizontal(true);
            isJawtomated = "Open";
        }

        SmartDashboard.putString("Jawtomatic", isJawtomated);
    }

    private void autoWrist(double distanceToCube)
    {
        if (distanceToCube < kCubeIn)  // TODO Call state.hasCube, make based on two counts at correct distance
        {
            intake.setVertical(true);
        }
    }
}
