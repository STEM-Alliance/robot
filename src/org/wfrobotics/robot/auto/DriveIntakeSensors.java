package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.DriveCommand;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveIntakeSensors extends DriveCommand
{
    private double settledSamples;
    private final double target;
    protected final double tol;
    protected double distance;

    public DriveIntakeSensors(double distanceFromTouchingObject, double tolerance)
    {
        requires(Robot.driveService);
        settledSamples = 0;
        target = distanceFromTouchingObject;
        tol = tolerance;
    }

    protected void initialize()
    {
        super.initialize();
        double sense = state.intakeDistance;
        double inches = (sense - Robot.config.INTAKE_DISTANCE_TO_BUMPER) * 2.54;
        distance = inches - target;
        SmartDashboard.putNumber("Sense", sense);
        SmartDashboard.putNumber("Distance", distance);
        SmartDashboard.putNumber("inches", inches);
        Robot.driveService.driveDistance(distance);
    }

    protected boolean isFinished()
    {
        final double remainingInches = Math.abs(distance) - Math.abs(state.robotDistanceDriven);

        if (Math.abs(remainingInches) < Math.abs(distance * tol))  // Assumes distance driven is in the direction of the commanded distance
        {
            settledSamples++;
        }
        else
        {
            settledSamples = 0;
        }
        return settledSamples > 20  || Robot.controls.getThrottle() > .15;  // Tight tolerances necessary in testing, commands cancel before settled otherwise - but talon not tuned so not sure
    }

    protected void end()
    {
        Robot.driveService.driveBasic(HerdVector.NEUTRAL);;
    }
}
