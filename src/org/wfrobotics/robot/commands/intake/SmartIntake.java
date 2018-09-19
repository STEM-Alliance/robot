package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.reuse.math.Util;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Pull in the cube purely based on sensors */
public class SmartIntake extends CommandGroup
{
    private final double kCubeIn = 5.0;
    private final double kCubeInDeadband = 1.25;

    protected final RobotState state = RobotState.getInstance();
    protected final Intake intake = Intake.getInstance();

    /**
     * This command gets the distance from the subsystem and based on that distance
     * drive the motors at different speeds
     */
    public SmartIntake()
    {
        requires(intake);
    }

    protected void execute()
    {
        if (state.liftHeightInches < 13.0 && state.wristAngleDegrees < 5.0)
        {
            double distanceToCube = state.intakeDistanceToCube;

            autoIntake(distanceToCube);
            autoJaws(distanceToCube);
        }
        else  // Cancel intaking if transition to lifting
        {
            intake.setMotors(0.0);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }

    private void autoIntake(double distanceToCube)
    {
        double speed = 0.0;
        SmartDashboard.putNumber("autoIntake something", 0);
        if (distanceToCube < kCubeInDeadband)
        {
            SmartDashboard.putNumber("autoIntake something", 1);
            intake.setMotors(0);
        }
        else if (distanceToCube < kCubeIn && distanceToCube > kCubeInDeadband)
        {
            SmartDashboard.putNumber("autoIntake something", 2);
            speed = -Util.scaleToRange(distanceToCube, kCubeInDeadband, kCubeIn, .25, 0.7);
        }
        else if (distanceToCube > kCubeIn && distanceToCube < 50/2.54)  // TODO Need to move sensor, otherwise we stall motors
        {
            SmartDashboard.putNumber("autoIntake something", 3);
            speed = -0.7;
        }

        // TODO After it's in for a little bit, it's SUPER effective to pulse the cube out a sec then back in to orient it
        //      This would also help us not stall if we don't drive wheels after that pulse. Or we move the distance sensor back enough to always be in a valid range.

        intake.setMotors(speed);
    }

    private void autoJaws(double distanceToCube)
    {
        if (distanceToCube < 30/2.54)
        {
            intake.setJaws(false);  // Can't always set, otherwise we chatter?
        }
        else if (distanceToCube > 30/2.54 && distanceToCube < 60/2.54)  // TODO find ideal range to be auto-opened, put in RobotMap or use robot state
        {
            intake.setJaws(true);
        }
    }
}
