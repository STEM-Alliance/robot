package com.taurus.swerve;

import com.taurus.Utilities;

import edu.wpi.first.wpilibj.Timer;

/**
 * Implementation of PIController for use with turning swerve wheel modules
 * 
 * @author Team 4818 Taurus Robotics
 *
 */
public final class SwerveAngleController {
    private static final double HalfCircle = 180, QuarterCircle = 90;
    private static final double MaxOut = 1;

    private static final double P = MaxOut / QuarterCircle * 3.0;
    private static final double T_I = .1; // seconds needed to equal a P term
                                          // contribution
    private static final double I = 0 / T_I;

    @SuppressWarnings("unused")
    private final String name;
    private final PIController controller;

    private double motorSpeed;
    private boolean reverseMotor;

    /**
     * Create a new angle controller with the default name
     */
    public SwerveAngleController()
    {
        this("SwerveAngleController");
    }

    /**
     * Create a new angle controller
     * 
     * @param name
     *            controller's name
     */
    public SwerveAngleController(String name)
    {
        this.name = name;
        this.motorSpeed = 0;
        this.reverseMotor = false;
        this.controller = new PIController(P, I, MaxOut);
    }

    /**
     * Reset the integral to 0. Useful when stopped to clear cumulative error
     */
    public void resetIntegral()
    {
        this.controller.integral = 0;
    }

    /**
     * Get the speed written to the motor
     * 
     * @return motor speed, between -1 and 1
     */
    public double getMotorSpeed()
    {
        return motorSpeed;
    }

    /**
     * Should the motor be driving in reverse? (180 vs 360 turning)
     * 
     * @return true if motor should be reversed
     */
    public boolean isReverseMotor()
    {
        return reverseMotor;
    }

    /**
     * Update the motor drive speed using the PI controller
     * 
     * @param setPoint
     *            desired position
     * @param sensorValue
     *            current position from sensor
     * @return new motor speed output, -1 to 1
     */
    public double update(double setPoint, double sensorValue)
    {
        // SmartDashboard.putNumber(name + ".SetPoint", setPoint);
        // SmartDashboard.putNumber(name + ".SensorValue", sensorValue);

        // Calculate error, with detection of the drive motor reversal shortcut.
        double error = calcErrorAndReverseNeeded(setPoint, sensorValue);

        // SmartDashboard.putNumber(name + ".Error", error);
        // SmartDashboard.putBoolean(name + ".Reverse", reverseMotor);

        motorSpeed = this.controller.update(error, Timer.getFPGATimestamp());

        // SmartDashboard.putNumber(name + ".Output", motorSpeed);

        return motorSpeed;
    }

    /**
     * Calculate the error from the current reading and desired position,
     * determine if motor reversal is needed
     * 
     * @param setPoint
     *            desired position
     * @param sensorValue
     *            current position from sensor
     * @return current error value
     */
    public double calcErrorAndReverseNeeded(double setPoint, double sensorValue)
    {
        // Calculate error (wrapped to +/- half circle).
        double error = setPoint - sensorValue;
        error = Utilities.wrapToRange(error, -HalfCircle, HalfCircle);

        // Calculate the error for the drive motor running backwards.
        double reversedMotorError = Utilities.wrapToRange(error + HalfCircle,
                -HalfCircle, HalfCircle);

        // Pick the smaller error.
        reverseMotor = Math.abs(reversedMotorError) < Math.abs(error);
        if (reverseMotor)
            error = reversedMotorError;

        return error;
    }

}
