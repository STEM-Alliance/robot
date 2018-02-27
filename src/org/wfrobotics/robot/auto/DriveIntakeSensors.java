package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drivebasic.DriveDistance;
import org.wfrobotics.robot.Robot;

public class DriveIntakeSensors extends DriveDistance
{
    private final double target;

    public DriveIntakeSensors(double distanceFromTouchingObject, double tolerance)
    {
        super(0.0);
        target = distanceFromTouchingObject;
        tol = tolerance;
    }

    protected void initialize()
    {
        distance = (Robot.config.INTAKE_DISTANCE_TO_BUMPER - state.intakeDistance) / 2.54 - target;
        super.initialize();
    }
}
