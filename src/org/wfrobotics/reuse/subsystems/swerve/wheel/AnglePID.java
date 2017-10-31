package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.PIDController;
import org.wfrobotics.reuse.utilities.Utilities;

import edu.wpi.first.wpilibj.Preferences;

/**
 * Absolute angle motor control PID
 * @author Team 4818 WFRobotics
 */
public final class AnglePID
{
    private static final double HalfCircle = 180;
    private static final double MaxOut = 1;

    private final HerdLogger log = new HerdLogger(AnglePID.class);
    private final PIDController controller;

    private double motorSpeed;
    private boolean reverseMotor;
    public double error;

    public AnglePID()
    {
        motorSpeed = 0;
        reverseMotor = false;
        controller = new PIDController(Config.ANGLE_P, Config.ANGLE_I, Config.ANGLE_D, MaxOut);
    }

    public String toString()
    {
        return String.format("(Speed: %.2f, Error: %.2f)", motorSpeed, error);
    }

    public void resetIntegral()
    {
        controller.resetError();
    }

    /**
     * Get the speed written to the motor
     * @return motor speed, between -1 and 1
     */
    public double getMotorSpeed()
    {
        return motorSpeed;
    }

    /**
     * Should the motor be driving in reverse? (180 vs 360 turning)
     * @return true if motor should be reversed
     */
    public boolean isReverseMotor()
    {
        return reverseMotor;
    }

    /**
     * Update the motor drive speed using the PI controller
     * @param setPoint desired position
     * @param sensorValue current position from sensor
     * @return new motor speed output, -1 to 1
     */
    public double update(double setPoint, double sensorValue)
    {
        updatePID();

        // Calculate error, with detection of the drive motor reversal shortcut.
        error = calcErrorAndReverseNeeded(setPoint, sensorValue);

        motorSpeed = controller.update(error);
        log.debug("Swerve AnglePID", this);

        return motorSpeed;
    }

    private void updatePID()
    {
        controller.setP(Preferences.getInstance().getDouble("WheelAngle_P", Config.ANGLE_P));
        controller.setI(Preferences.getInstance().getDouble("WheelAngle_I", Config.ANGLE_I));
        controller.setD(Preferences.getInstance().getDouble("WheelAngle_D", Config.ANGLE_D));
    }

    /**
     * Calculate the error from the current reading and desired position, determine if motor reversal is needed
     * @param setPoint desired position
     * @param sensorValue current position from sensor
     * @return current error value
     */
    public double calcErrorAndReverseNeeded(double setPoint, double sensorValue)
    {
        double error = Utilities.wrapToRange(setPoint - sensorValue, -HalfCircle, HalfCircle);
        double reversedMotorError = Utilities.wrapToRange(error + HalfCircle, -HalfCircle, HalfCircle);

        reverseMotor = Math.abs(reversedMotorError) < Math.abs(error);
        if (reverseMotor)
        {
            error = reversedMotorError;
        }

        return error;
    }
}
