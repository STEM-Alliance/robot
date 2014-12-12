package com.taurus;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class SwerveAngleController
{
    private static double ErrorMax = 180;
    private static double MaxOut = 1;
    private static double P = 0.0001;
    private static double I = 0.0; // FIXME: P / 100

    private String Name;
    private double DriveMotorSpeed;
    private boolean ReverseDriveMotor;
    private double IntegratedError;

    public SwerveAngleController()
    {
        this("SwerveAngleController");
    }
    
    public SwerveAngleController(String name)
    {
        this.Name = name;
        this.Reset();
    }
    
    public void Reset()
    {
        this.DriveMotorSpeed = 0;
        this.ReverseDriveMotor = false;
        this.IntegratedError = 0;
    }

    public double getAngleMotorSpeed()
    {
        return DriveMotorSpeed;
    }

    public boolean isReverseDriveMotor()
    {
        return ReverseDriveMotor;
    }

    public void Update(double setPoint, double sensorValue)
    {
        SmartDashboard.putNumber(Name + ".SetPoint", setPoint);
        SmartDashboard.putNumber(Name + ".SensorValue", sensorValue);

        // Calculate error, with detection of the drive motor reversal shortcut.
        double error = CalcErrorAndReverseNeeded(setPoint, sensorValue);
        
        SmartDashboard.putNumber(Name + ".Error", error);
        SmartDashboard.putBoolean(Name + ".Reverse", ReverseDriveMotor);

        // Calculate integral term.
        IntegratedError += error * I;
        IntegratedError = Utilities.clampToRange(IntegratedError, -MaxOut, MaxOut);
        
        SmartDashboard.putNumber(Name + ".IntegratedError", IntegratedError);

        // Calculate output with coefficients.
        double output = error * P + IntegratedError;
        
        SmartDashboard.putNumber(Name + ".Output", output);

        // Clamp output.
        DriveMotorSpeed = Utilities.clampToRange(output, -MaxOut, MaxOut);
        
        SmartDashboard.putNumber(Name + ".ClampedOutput", DriveMotorSpeed);
    }

    public double CalcErrorAndReverseNeeded(double setPoint, double sensorValue)
    {
        // Calculate error.
        double error = setPoint - sensorValue;
        error = Utilities.wrapToRange(error, -ErrorMax, ErrorMax);
        
        // Adjust error for shortest path (possibly including reversing the
        // drive motor direction).
        double reversedMotorError = Utilities.wrapToRange(error + ErrorMax, -ErrorMax, ErrorMax);
        ReverseDriveMotor = false;
        
        if (Math.abs(reversedMotorError) < Math.abs(error))
        {
            ReverseDriveMotor = true;
            error = reversedMotorError;
        }
        
        return error;
    }

}
