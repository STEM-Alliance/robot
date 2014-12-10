package com.taurus;

public class SwerveAngleController
{
    private static double MinIn = -180, MaxIn = 180, MinOut = -1, MaxOut = 1;
    private static double P = 
            MaxOut /* Max speed (Note: 1.2 rot/second or 430 degrees/second) */
            * 2.0 /* Speed fraction at max distance (before clamping) */
            * 4 / (MaxIn - MinIn) /* Divide by max distance to travel (quarter circle) */;

    private double DriveMotorSpeed;
    private boolean ReverseDriveMotor;

    public double getDriveMotorSpeed()
    {
        return DriveMotorSpeed;
    }

    public boolean isReverseDriveMotor()
    {
        return ReverseDriveMotor;
    }

    public void Update(double setPoint, double sensorValue)
    {
        double error = CalcErrorAndReverseNeeded(setPoint, sensorValue);

        // Calculate output with coefficients.
        double output = error * P;

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
            error -= Math.copySign(MaxIn - MinIn, error);
        }
        else if (Math.abs(error) > (MaxIn - MinIn) / 4)
        {
            // greater than 1/4 circle -> less than 1/4 circle + motor reverse
            ReverseDriveMotor = true;
            error -= Math.copySign((MaxIn - MinIn) / 2, error);
        }
        
        return error;
    }

}
