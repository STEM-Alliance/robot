package com.taurus;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveAngleController
{
    private static double MinIn = -180, MaxIn = 180, MinOut = -1, MaxOut = 1;
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
        
        double error = CalcErrorAndReverseNeeded(setPoint, sensorValue);
        
        SmartDashboard.putNumber(Name + ".Error", error);
        SmartDashboard.putBoolean(Name + ".Reverse", ReverseDriveMotor);

        // Calculate integral term.
        IntegratedError += error * I;
        IntegratedError = Utilities.clampToRange(IntegratedError, MinOut, MaxOut);
        SmartDashboard.putNumber(Name + ".IntegratedError", IntegratedError);

        // Calculate output with coefficients.
        double output = error * P + IntegratedError;
        SmartDashboard.putNumber(Name + ".Output", output);

        // Clamp output.
        DriveMotorSpeed = Utilities.clampToRange(output, MinOut, MaxOut);
        SmartDashboard.putNumber(Name + ".ClampedOutput", DriveMotorSpeed);
    }

    public double CalcErrorAndReverseNeeded(double setPoint, double sensorValue)
    {
        // Clamp input.
        setPoint = Utilities.clampToRange(setPoint, MinIn, MaxIn);
        sensorValue = Utilities.clampToRange(sensorValue, MinIn, MaxIn);

        // Calculate error.
        double error = setPoint - sensorValue;

        // Adjust error for shortest path (possibly including reversing the
        // drive motor direction).
        ReverseDriveMotor = false;
        if (Math.abs(error) > (MaxIn - MinIn) * 3 / 4)
        {
            // greater than 3/4 circle -> less than 1/4 circle
            error -= Utilities.copySign(MaxIn - MinIn, error);
        }
        else if (Math.abs(error) > (MaxIn - MinIn) / 4)
        {
            // greater than 1/4 circle -> less than 1/4 circle + motor reverse
            ReverseDriveMotor = true;
            error -= Utilities.copySign((MaxIn - MinIn) / 2, error);
        }
        
        return error;
    }

}
