package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.reuse.math.Util;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.Lift;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.CommandGroup;

/** Pull in the cube purely based on sensors */
public class SmartIntake extends CommandGroup
{
    private static final double kCubeIn = 5.0;
    private static final double kCubeInDeadband = 1.25;
    private static final double kCubeWant = 50/2.54;
    private static final double kJawsNom = 30/2.54;
    private static final double kJawsHungry = 60/2.54;
    private static final double kSpeedNom = 0.25;
    private static final double kSpeedHungry = 0.7;

    private final Intake intake = Intake.getInstance();
    private final Lift lift = Lift.getInstance();
    private final Wrist wrist = Wrist.getInstance();

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
        if (lift.getPosition() < 13.0 && wrist.getPosition() < 5.0)
        {
            final double distanceToCube = intake.getCubeDistance();

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

        if (distanceToCube < kCubeInDeadband)
        {
            intake.setMotors(0);
        }
        else if (distanceToCube < kCubeIn && distanceToCube > kCubeInDeadband)
        {
            speed = -Util.scaleToRange(distanceToCube, kCubeInDeadband, kCubeIn, kSpeedNom, kSpeedHungry);
        }
        else if (distanceToCube > kCubeIn && distanceToCube < kCubeWant)
        {
            speed = -kSpeedHungry;
        }
        intake.setMotors(speed);
    }

    private void autoJaws(double distanceToCube)
    {
        if (distanceToCube < kJawsNom)
        {
            intake.setJaws(false);  // Can't always set, otherwise we chatter?
        }
        else if (distanceToCube > kJawsNom && distanceToCube < kJawsHungry)
        {
            intake.setJaws(true);
        }
    }
}
