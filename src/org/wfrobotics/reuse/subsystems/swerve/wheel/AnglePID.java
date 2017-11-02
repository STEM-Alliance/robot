package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.utilities.PIDController;
import org.wfrobotics.reuse.utilities.Utilities;

/**
 * Absolute angle motor control PID
 * @author Team 4818 WFRobotics
 */
public final class AnglePID
{
    private static final double HalfCircle = 180;

    private final PIDController controller;

    private double motorSpeed;
    private boolean reverseMotor;
    public double errorShortestPath;

    public AnglePID(double p, double i, double d, double max)
    {
        controller = new PIDController(p, i, d, max);
        motorSpeed = 0;
        reverseMotor = false;
    }

    public String toString()
    {
        return String.format("(Speed: %.2f, Error: %.2f)", motorSpeed, errorShortestPath);
    }

    /**
     * Update the motor drive speed using the PI controller
     * @param setPoint desired position
     * @param sensorValue current position from sensor
     * @return new motor speed output, -1 to 1
     */
    public double update(double setPoint, double sensorValue)
    {
        double forwardMotorError = Utilities.wrapToRange(setPoint - sensorValue, -HalfCircle, HalfCircle);
        double reversedMotorError = Utilities.wrapToRange(forwardMotorError + HalfCircle, -HalfCircle, HalfCircle);

        reverseMotor = Math.abs(reversedMotorError) < Math.abs(forwardMotorError);

        errorShortestPath = (reverseMotor) ? reversedMotorError : forwardMotorError;
        // TODO if we reverse, reset the integral
        motorSpeed = controller.update(errorShortestPath);

        return motorSpeed;
    }

    public void updatePID(double p, double i, double d)
    {
        controller.setP(p);
        controller.setI(i);
        controller.setD(d);
    }

    public void resetIntegral()
    {
        controller.resetError();
    }

    /**
     * Should the motor be driving in reverse? (180 vs 360 turning)
     * @return true if motor should be reversed
     */
    public boolean reverseDriveMotorMoreEfficient()
    {
        return reverseMotor;
    }
}
