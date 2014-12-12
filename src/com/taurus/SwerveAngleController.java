package com.taurus;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class SwerveAngleController
{
    private static final double MaxTimestampDiff = 1.0;  // heuristic to detect disabling
    private static final double HalfCircle = 180, QuarterCircle = 90;
    private static final double MaxOut = 1;
    
    private static final double P = MaxOut / QuarterCircle * 0.01;
    private static final double T_I = .5;  // seconds needed to equal a P term contribution
    private static final double I = 0;//1 / T_I;

    private String Name;
    private double DriveMotorSpeed;
    private boolean ReverseDriveMotor;
    private double Integral;
    private double LastTimestamp = 0;

    public SwerveAngleController()
    {
        this("SwerveAngleController");
    }
    
    public SwerveAngleController(String name)
    {
        this.Name = name;
        this.LastTimestamp = 0;
        this.Reset();
    }
    
    public void Reset()
    {
        this.DriveMotorSpeed = 0;
        this.ReverseDriveMotor = false;
        this.Integral = 0;
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

        // Proportional term.
        double proportional = error * P;
        
        SmartDashboard.putNumber(Name + ".Proportional", proportional);
        
        // Get time since we were last called.
        double timeDelta;
        {
            double timestamp = Timer.getFPGATimestamp();
            timeDelta = timestamp - LastTimestamp;
            LastTimestamp = timestamp;
        }
        
        // Calculate integral term.
        if (timeDelta > MaxTimestampDiff)
        {
            // We were probably disabled; reset the integral error.
            Integral = 0;
        }
        else
        {
            // Sum the proportional term over time.
            Integral += proportional * timeDelta * I;
            Integral = Utilities.clampToRange(Integral, -MaxOut, MaxOut);
        }
        
        SmartDashboard.putNumber(Name + ".IntegratedError", Integral);

        // Calculate output with coefficients.
        double output = proportional + Integral;
        
        SmartDashboard.putNumber(Name + ".Output", output);

        // Clamp output.
        DriveMotorSpeed = Utilities.clampToRange(output, -MaxOut, MaxOut);
        
        SmartDashboard.putNumber(Name + ".ClampedOutput", DriveMotorSpeed);
    }

    public double CalcErrorAndReverseNeeded(double setPoint, double sensorValue)
    {
        // Calculate error (wrapped to +/- half circle).
        double error = setPoint - sensorValue;
        error = Utilities.wrapToRange(error, -HalfCircle, HalfCircle);
        
        // Calculate the error for the drive motor running backwards.
        double reversedMotorError = Utilities.wrapToRange(error + HalfCircle, -HalfCircle, HalfCircle);
        
        // Pick the smaller error.
        ReverseDriveMotor = Math.abs(reversedMotorError) < Math.abs(error);
        if (ReverseDriveMotor)
            error = reversedMotorError;
        
        return error;
    }

}
