package org.wfrobotics.reuse.subsystems.swerve;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.utilities.PIDController;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Implementation of {@link PIDController} for use with turning swerve wheel modules
 * @author Team 4818 WFRobotics
 */
public final class WheelAngleController 
{
    private static final boolean DEBUG = false;
    
    private static final double HalfCircle = 180;
    private static final double MaxOut = 1;

    private final String name;
    private final PIDController controller;

    private double motorSpeed;
    private boolean reverseMotor;
    public double error;

    public WheelAngleController()
    {
        this("SwerveAngleController");
    }

    public WheelAngleController(String name)
    {
        this.name = name;
        this.motorSpeed = 0;
        this.reverseMotor = false;

        this.controller = new PIDController(Constants.ANGLE_P, 
                                            Constants.ANGLE_I,
                                            Constants.ANGLE_D,
                                            MaxOut);    
    }

    /**
     * Reset the integral to 0. Useful when stopped to clear cumulative error
     */
    public void resetIntegral()
    {
        this.controller.resetError();
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

        motorSpeed = this.controller.update(error);

        if(DEBUG)
        {
            SmartDashboard.putNumber(name + ".SetPoint", setPoint);
            SmartDashboard.putNumber(name + ".SensorValue", sensorValue);
            SmartDashboard.putNumber(name + ".Error", error);
            SmartDashboard.putBoolean(name + ".Reverse", reverseMotor);
            SmartDashboard.putNumber(name + ".Output", motorSpeed);
        }
        
        return motorSpeed;
    }

    private void updatePID()
    {
        controller.setP(Preferences.getInstance().getDouble("WheelAnglePID_P", Constants.ANGLE_P));
        controller.setI(Preferences.getInstance().getDouble("WheelAnglePID_I", Constants.ANGLE_I));
        controller.setD(Preferences.getInstance().getDouble("WheelAnglePID_D", Constants.ANGLE_D));
    }

    /**
     * Calculate the error from the current reading and desired position,
     * determine if motor reversal is needed
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
