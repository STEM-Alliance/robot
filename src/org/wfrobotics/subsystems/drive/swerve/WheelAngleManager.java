package org.wfrobotics.subsystems.drive.swerve;

import org.wfrobotics.Utilities;
import org.wfrobotics.hardware.MagnetoPot;
import org.wfrobotics.hardware.MagnetoPotSRX;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.AllocationException;

public class WheelAngleManager 
{
    private static final double HalfCircle = 180;
    
    private CANTalon angleMotor;
    private MagnetoPot anglePot;
    /** Invert the angle motor and sensor to swap left/right */
    private boolean angleInverted = true;

    private static final boolean DEBUG = false;
    private String name;
    private double motorSpeed;
    private boolean reverseMotor;
    public double error;
    
    public WheelAngleManager(String name, int talonAddress)
    {
        this.name = name;
        angleMotor = new CANTalon(talonAddress);
        angleMotor.setVoltageRampRate(30);
        angleMotor.ConfigFwdLimitSwitchNormallyOpen(true);
        angleMotor.ConfigRevLimitSwitchNormallyOpen(true);
        angleMotor.enableForwardSoftLimit(false);
        angleMotor.enableReverseSoftLimit(false);
        angleMotor.enableBrakeMode(false);
        angleMotor.configNominalOutputVoltage(SwerveWheel.MINIMUM_SPEED, -SwerveWheel.MINIMUM_SPEED);  // Hardware deadband in closed-loop modes
        //angleMotor.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_50Ms);
        //angleMotor.SetVelocityMeasurementWindow(32);type name = new type();
        
        //anglePot = new MagnetoPotSRX(angleMotor, 360);
        
        angleMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
        angleMotor.changeControlMode(TalonControlMode.Position);
        angleMotor.setPID(SwerveConstants.ANGLE_PID_P,
                          SwerveConstants.ANGLE_PID_I,
                          SwerveConstants.ANGLE_PID_D,
                          0,
                          0,
                          10,
                          0);
    }
    
    /**
     * Update the motor drive speed using the PI controller
     * 
     * @param setPoint
     *            desired position
     * @param sensorValue
     *            current position from sensor
     * @return new motor speed output, -1 to 1
     */
    public double update(double setPoint, double sensorValue)
    {
        error = calcErrorAndReverseNeeded(setPoint, sensorValue);

        motorSpeed = getSpeed();

        if(DEBUG)
        {
            SmartDashboard.putNumber(name + ".SetPoint", setPoint);
            SmartDashboard.putNumber(name + ".SensorValue", sensorValue);
            SmartDashboard.putNumber(name + ".Error", error);
            SmartDashboard.putBoolean(name + ".Reverse", reverseMotor);
            SmartDashboard.putNumber(name + ".Output", motorSpeed);
        }

        return motorSpeed;
    }
    
    public void set(double speed)
    {
        angleMotor.set(speed);
    }

    public double getAnglePotAdjusted()
    {
        double invert = angleInverted ? -1 : 1;
        
        return Utilities.round(Utilities.wrapToRange(invert * angleMotor.getPosition(), -180, 180),2);
    }
    
    public void setPotOffset(double offset)
    {
        angleMotor.setPosition(offset);
    }
    
    public double debugGetPotRaw()
    {
        return angleMotor.getEncPosition();
    }
    
    public double getSpeed()
    {
        return angleMotor.getSpeed();  // TODO this is in counts
    }
    
    /**
     * Reset the integral to 0. Useful when stopped to clear cumulative error
     */
    public void resetIntegral()
    {
        angleMotor.clearIAccum();
    }
    
    private void updatePID()
    {
        angleMotor.setP(Preferences.getInstance().getDouble("WheelAnglePID_P", SwerveConstants.ANGLE_PID_P));
        angleMotor.setI(Preferences.getInstance().getDouble("WheelAnglePID_I", SwerveConstants.ANGLE_PID_I));
        angleMotor.setD(Preferences.getInstance().getDouble("WheelAnglePID_D", SwerveConstants.ANGLE_PID_D));
    }
    
    /**
     * Should the motor be driving in reverse? (180 vs 360 turning)
     * 
     * @return true if motor should be reversed
     */
    public boolean isReverseMotor()
    {
        return reverseMotor;
    }
    
    /**
     * Calculate the error from the current reading and desired position,
     * determine if motor reversal is needed
     * 
     * @param setPoint
     *            desired position
     * @param sensorValue
     *            current position from sensor
     * @return current error value
     */
    public double calcErrorAndReverseNeeded(double setPoint, double sensorValue)
    {
        // Calculate error (wrapped to +/- half circle).
        double error = setPoint - sensorValue;
        error = Utilities.wrapToRange(error, -HalfCircle, HalfCircle);

        // Calculate the error for the drive motor running backwards.
        double reversedMotorError = Utilities.wrapToRange(error + HalfCircle,
                -HalfCircle, HalfCircle);

        // Pick the smaller error.
        reverseMotor = Math.abs(reversedMotorError) < Math.abs(error);
        if (reverseMotor)
            error = reversedMotorError;

        return error;
    }
    
    /**
     * For unit tests until we mock the pot
     */
    public void free()
    {
        //try { anglePot.free(); } catch (AllocationException e) {}
    }
}