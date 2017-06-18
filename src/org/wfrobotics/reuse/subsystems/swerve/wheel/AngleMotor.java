package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.Vector;
import org.wfrobotics.reuse.utilities.HerdLogger;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Preferences;

// TODO Consider owning the specific angle motor, rather than subclassing this

/**
 * Controls swerve wheel turning
 * @author Team 4818 WFRobotics
 */
public abstract class AngleMotor 
{
    protected final String NAME;
    
    private HerdLogger log = new HerdLogger(AngleMotor.class);
    private final AnglePID anglePID;
    protected final CANTalon motor;
    
    /** Invert the angle motor and sensor to swap left/right */
    protected boolean angleInverted = false;
    
    public AngleMotor(String name, int talonAddress)
    {
        NAME = name;
        anglePID = new AnglePID();
        motor = new CANTalon(talonAddress);
        motor.configNominalOutputVoltage(0, 0);
        motor.configPeakOutputVoltage(11, -11);
        motor.ConfigFwdLimitSwitchNormallyOpen(true);
        motor.ConfigRevLimitSwitchNormallyOpen(true);
        motor.enableForwardSoftLimit(false);
        motor.enableReverseSoftLimit(false);
        motor.enableBrakeMode(false);
        //angleMotor.setVoltageRampRate(30);
        //angleMotor.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_50Ms);
        //angleMotor.SetVelocityMeasurementWindow(32);
        //angleMotor.setStatusFrameRateMs(StatusFrameRate.Feedback, 10);
    }
    
    public abstract void set(double speed);
    public abstract double getDegrees();
    public abstract void setSensorOffset(double degrees);
    
    public String toString()
    {
        return String.format("A: %.2f\u00b0, E: %.2f\u00b0", getDegrees(), anglePID.error);
    }
    
    /**
     * Update the angle motor based on the desired angle called from updateTask()
     * @return Whether the drive motor should run in the opposite direction
     */
    public boolean update(Vector desired)
    {
        double angleOffset = Preferences.getInstance().getDouble(NAME + ".Offset", 0);
        
        setSensorOffset(angleOffset);
        
        if (desired.getMag() > Constants.DEADBAND_MINIMUM_SPEED)
        {
            double setpoint = desired.getAngle();
            double current = getDegrees();
            double angleMaxSpeed = Preferences.getInstance().getDouble("maxRotationSpeed", Constants.ANGLE_MAX_SPEED);
            
            anglePID.update(setpoint, current);
            set(anglePID.getMotorSpeed() * angleMaxSpeed);
        }
        else
        {
            anglePID.resetIntegral();
            set(0);
        }     

        log.debug(NAME, this);
        
        return anglePID.isReverseMotor();
    }
}
