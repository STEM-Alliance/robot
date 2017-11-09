package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.utilities.Utilities;

import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

//TODO Use Talon PID for encoder controlled angle motor?

public class AngleMotorEncoder extends AngleMotor
{
    private double angleOffset = 0;

    /** Invert the angle motor and sensor to swap left/right */
    protected boolean angleInverted = true;

    public AngleMotorEncoder(String name, int talonAddress)
    {
        super(name, talonAddress);

        motor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
        motor.changeControlMode(TalonControlMode.PercentVbus);
        //angleMotor.setPosition(angleMotor.getPosition());

        // TODO Set the initial position, switch to relative mode --> Test this, should produce 4x HW closed loop speedup
    }

    public double getDegrees()
    {
        double invert = angleInverted ? 1 : -1;
        double degrees = motor.getPosition() * 360;
        double wrappedDegrees = Utilities.wrapToRange(degrees - angleOffset, -180, 180);

        return invert * wrappedDegrees;
    }

    public void setSensorOffset(double degrees)
    {
        angleOffset = degrees;
    }
}