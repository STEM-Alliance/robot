package org.wfrobotics.reuse.subsystems.motors;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.subsystems.motors.ConfigMotor.ConfigMotorBulider;

/**
 * CANTalon motor to be used with a CTRE Magnetic Encoder in rotation/angle mode.
 * Uses the PID built into the CANTalon.
 */
public class HerdMotorRotationEncoder extends HerdMotorRotation {

    private double angleOffset = 0;
    
    public HerdMotorRotationEncoder(ConfigMotorBulider configMotorBuilder)
    {
        this(configMotorBuilder.build());
    }
    
    public HerdMotorRotationEncoder(ConfigMotor configMotor)
    {
        super(configMotor);
        
        setPID(configMotor.configPID.p,
               configMotor.configPID.i,
               configMotor.configPID.d,
               configMotor.configPID.f,
               configMotor.configPID.iZone,
               configMotor.configPID.rampRate,
               0);
         
        setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
        changeControlMode(TalonControlMode.Position);
        
        //TODO is this right?
        enableControl();
        super.set(getPosition());  // Recommended for closed loop control     
    }
    
    /**
     * set the angle offset
     * @param offset in degrees
     */
    @Override
    public void setOffset(double offset)
    {
        angleOffset = offset;
    }
    
    /**
     * Get the current angle in degrees
     * @return angle in degrees (0-360)
     */
    @Override
    public double get()
    {
        return Utilities.round(Utilities.wrapToRange(getInvertedValue() * getSensor(), 0, 360), 2);
    }
    
    /**
     * Set the motor output to the desired angle in degrees
     * @param angle angle in degrees
     */
    @Override
    public void set(double angle)
    {
        //TODO this is most likely wrong
        super.set(angle / 360.0);
    }
    

    private double getSensor()
    {
        double degrees = getPosition() * 360.0;
        
        return Utilities.wrapToRange(degrees - angleOffset, -180, 180);
    }
}
