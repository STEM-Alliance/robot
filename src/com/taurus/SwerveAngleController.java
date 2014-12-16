package com.taurus;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class SwerveAngleController
{
    private static final double HalfCircle = 180, QuarterCircle = 90;
    private static final double MaxOut = 1;
    
    private static final double P = MaxOut / QuarterCircle * 0.4;
    //private static final double T_I = .5;  // seconds needed to equal a P term contribution
    private static final double I = 0;//1 / T_I;

    private final String name;
    private final PIController controller;
    
    private double driveMotorSpeed;
    private boolean reverseDriveMotor;

    public SwerveAngleController()
    {
        this("SwerveAngleController");
    }
    
    public SwerveAngleController(String name)
    {
        this.name = name;
        this.driveMotorSpeed = 0;
        this.reverseDriveMotor = false;
        this.controller = new PIController(P, I, MaxOut);
    }

    public void resetIntegral()
    {
        this.controller.integral = 0;
    }
    
    public double getAngleMotorSpeed()
    {
        return driveMotorSpeed;
    }

    public boolean isReverseDriveMotor()
    {
        return reverseDriveMotor;
    }

    public void Update(double setPoint, double sensorValue)
    {
        SmartDashboard.putNumber(name + ".SetPoint", setPoint);
        SmartDashboard.putNumber(name + ".SensorValue", sensorValue);

        // Calculate error, with detection of the drive motor reversal shortcut.
        double error = CalcErrorAndReverseNeeded(setPoint, sensorValue);
        
        SmartDashboard.putNumber(name + ".Error", error);
        SmartDashboard.putBoolean(name + ".Reverse", reverseDriveMotor);

        driveMotorSpeed = this.controller.update(error, Timer.getFPGATimestamp());
        
        SmartDashboard.putNumber(name + ".Output", driveMotorSpeed);
    }

    public double CalcErrorAndReverseNeeded(double setPoint, double sensorValue)
    {
        // Calculate error (wrapped to +/- half circle).
        double error = setPoint - sensorValue;
        error = Utilities.wrapToRange(error, -HalfCircle, HalfCircle);
        
        // Calculate the error for the drive motor running backwards.
        double reversedMotorError = Utilities.wrapToRange(error + HalfCircle, -HalfCircle, HalfCircle);
        
        // Pick the smaller error.
        reverseDriveMotor = Math.abs(reversedMotorError) < Math.abs(error);
        if (reverseDriveMotor)
            error = reversedMotorError;
        
        return error;
    }

}
