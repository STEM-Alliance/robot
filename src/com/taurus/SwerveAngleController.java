package com.taurus;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveAngleController
{
    private static double MinIn = -180, MaxIn = 180, MinOut = -1, MaxOut = 1;
    private static double MaxError = 90;
    private static double P = 0.0001;
    private static double I = 0.0;//P / 100;

    private double DriveMotorSpeed;
    private boolean ReverseDriveMotor;
    private double IntegralError = 0.0d;

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
        //P = SmartDashboard.getNumber("P", P);
        //I = SmartDashboard.getNumber("I", P);
        
        double error = CalcErrorAndReverseNeeded(setPoint, sensorValue);

        SmartDashboard.putNumber(".ctl.Error", error);
        
        // Add up error.
        IntegralError += error;
        
        // Clamp to MaxError.
        IntegralError = Math.min(MaxOut / I, Math.max(MinOut / I, IntegralError));
        
        SmartDashboard.putNumber(".ctl.Integral", IntegralError);
        
        // Calculate output with coefficients.
        double output = error * P + IntegralError * I;

        // Clamp output.
        DriveMotorSpeed = Math.max(MinOut, Math.min(MaxOut, output));
    }

    public double CalcErrorAndReverseNeeded(double setPoint, double sensorValue)
    {
        // Clamp input.
        setPoint = Math.max(MinIn, Math.min(MaxIn, setPoint));
        sensorValue = Math.max(MinIn, Math.min(MaxIn, sensorValue));

        // Calculate error.
        double error = (setPoint - MinIn) - (sensorValue - MinIn);

        // Adjust error for shortest path (possibly including reversing the
        // drive motor direction).
        ReverseDriveMotor = false;
        if (Math.abs(error) > (MaxIn - MinIn) * 3 / 4)
        {
            // greater than 3/4 circle -> less than 1/4 circle
            error -= copySign(MaxIn - MinIn, error);
        }
        else if (Math.abs(error) > (MaxIn - MinIn) / 4)
        {
            // greater than 1/4 circle -> less than 1/4 circle + motor reverse
            ReverseDriveMotor = true;
            error -= copySign((MaxIn - MinIn) / 2, error);
        }
        
        return error;
    }
    
    private static double copySign(double magnitude, double sign)
    {
        if ((sign < 0) != (magnitude < 0))
            return -magnitude;
        return magnitude;
    }

}
