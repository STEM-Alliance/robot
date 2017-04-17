package org.wfrobotics.reuse.subsystems.swerve;

import org.wfrobotics.Utilities;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;


public class WheelAngleEncoderMotor implements WheelAngleMotor
{
    private CANTalon angleMotor;
    /** Invert the angle motor and sensor to swap left/right */
    private boolean angleInverted = false;
    
    private double angleOffset = 0;
    
    public WheelAngleEncoderMotor(int talonAddress)
    {
        angleMotor = new CANTalon(talonAddress);
        //angleMotor.setVoltageRampRate(30);
        angleMotor.configNominalOutputVoltage(0, 0);
        angleMotor.configPeakOutputVoltage(11, -11);
        angleMotor.ConfigFwdLimitSwitchNormallyOpen(true);
        angleMotor.ConfigRevLimitSwitchNormallyOpen(true);
        angleMotor.enableForwardSoftLimit(false);
        angleMotor.enableReverseSoftLimit(false);
        angleMotor.enableBrakeMode(false);
        //angleMotor.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_50Ms);
        //angleMotor.SetVelocityMeasurementWindow(32);

        //angleMotor.setStatusFrameRateMs(StatusFrameRate.Feedback, 10);
        angleMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
        angleMotor.changeControlMode(TalonControlMode.PercentVbus);
        //angleMotor.setPosition(angleMotor.getPosition());
    }
    
    public void set(double speed)
    {
        double invert = angleInverted ? 1 : -1;
        angleMotor.set(invert * speed);
    }

    public double getAnglePotAdjusted()
    {
        double invert = angleInverted ? -1 : 1;
        
        return Utilities.round(invert * getSensor(), 2);
    }
    
    public void setPotOffset(double offset)
    {
        angleOffset = offset;
    }
    
    public double debugGetPotRaw()
    {
        return 0;//anglePot.getRawInput();
    }
    
    /**
     * For unit tests until we mock the pot
     */
    public void free()
    {
        
    }
    
    private double getSensor()
    {
        double degrees = angleMotor.getPosition() * 360.0;
        
        return Utilities.wrapToRange(degrees - angleOffset, -180, 180);
    }
}