package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.Utilities;

import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

public class AngleMotorEncoder extends AngleMotor
{
    private double angleOffset = 0;

    public AngleMotorEncoder(String name, int talonAddress)
    {
        super(name, talonAddress);

        motor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
        motor.changeControlMode(TalonControlMode.PercentVbus);
        //angleMotor.setPosition(angleMotor.getPosition());

        // TODO Set the initial position, switch to relative mode --> Test this, should produce 4x HW closed loop speedup
    }

    public void set(double speed)
    {
        double invert = angleInverted ? 1 : -1;
        motor.set(invert * speed);
    }

    public double getDegrees()
    {
        double invert = angleInverted ? -1 : 1;

        return round(invert * getWrappedDegrees(), 2);
    }

    public void setSensorOffset(double degrees)
    {
        angleOffset = degrees;
    }

    private double getWrappedDegrees()
    {
        double degrees = motor.getPosition() * 360.0;

        return Utilities.wrapToRange(degrees - angleOffset, -180, 180);
    }

    /**
     * Round a value to the desired number of decimal places
     * 
     * @param d
     *            value to round
     * @param res
     *            number of decimal places
     * @return
     */
    public double round(double d, int res)
    {
        int x = (int) Math.pow(10, res);
        return Math.rint(d * x) / x;
    }
}