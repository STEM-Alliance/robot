package org.wfrobotics.subsystems.drive.swerve;

import org.wfrobotics.Utilities;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WheelAngleManager 
{
    private static final double HalfCircleCounts = 4096/2;
    private static final double HalfCircleDegrees = 180;
    private static final double CountsPerDegree = 4096/360;
    private final double ANGLE_SW_DEADBAND = 10;  // Make this the fastest wheels angle error while not moving
    
    private final CANTalon talon;
    /** Invert the angle motor and sensor to swap left/right */
    private final boolean angleInverted = true;
    private final String name;
    private static final boolean DEBUG = true;

    private double offset = 0;
    private boolean reverseMotor = false;
    private boolean inCachedMode = false;
    private double cachedAngle = 0;
    
    public WheelAngleManager(String name, int talonAddress, boolean invert)
    {
        this.name = name;
        talon = new CANTalon(talonAddress);
        //talon.setVoltageRampRate(60);
        talon.ConfigFwdLimitSwitchNormallyOpen(true);
        talon.ConfigRevLimitSwitchNormallyOpen(true);
        talon.enableForwardSoftLimit(false);
        talon.enableReverseSoftLimit(false);
        talon.enableBrakeMode(false);
        talon.configNominalOutputVoltage(0, 0);
        talon.configPeakOutputVoltage(11, -11);
        //angleMotor.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_50Ms);
        //angleMotor.SetVelocityMeasurementWindow(32);type name = new type();
        
        talon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
        talon.changeControlMode(TalonControlMode.Position);
        talon.setPID(SwerveConstants.ANGLE_PID_P, SwerveConstants.ANGLE_PID_I, SwerveConstants.ANGLE_PID_D,
                          0, 0, 10, 0);
        //talon.reverseSensor(invert);
        talon.setInverted(invert);
        talon.setAllowableClosedLoopErr(0);
        talon.enableControl();
        talon.set(getCurrentAngle());
    }
    
    public void setStationary()
    {
        talon.changeControlMode(TalonControlMode.PercentVbus);
        talon.set(0);
    }
    
    public void setPosition(double desiredDegrees, boolean stationary)
    {        
      double desiredCounts = desiredDegrees * CountsPerDegree + offset;
      double error;
      double commanded = -1;
      
      talon.changeControlMode(TalonControlMode.Position);
      error = Utilities.wrapToRange(desiredCounts - talon.getPosition(), -HalfCircleCounts, HalfCircleCounts);
      
      if (Math.abs(error) < ANGLE_SW_DEADBAND ||
          (stationary && inCachedMode == false))
      {
          cachedAngle = talon.getPosition();
          inCachedMode = true;
      }
      else if (!stationary)
      {
          inCachedMode = false;
      }
      
      if (inCachedMode)
      {
          commanded = cachedAngle;
      }
      else
      {
          commanded = applySmartReverse(desiredCounts, error);
      }
        
      talon.set(commanded);
      
      if(DEBUG)
      {
          SmartDashboard.putNumber(name + ".get", talon.getPosition());
          SmartDashboard.putNumber(name + ".Desired", desiredDegrees);
//          SmartDashboard.putNumber(name + ".SensorValue", sensorValue);
//          SmartDashboard.putNumber(name + ".Error", error);
          SmartDashboard.putNumber(name + ".Cached", cachedAngle);
          SmartDashboard.putNumber(name + ".Commanded", commanded / CountsPerDegree);
          SmartDashboard.putNumber(name + ".angle.err", talon.getClosedLoopError() & 0xFFF);
      }
    }
    
    public double getCurrentAngle()
    {
        //return Utilities.wrapToRange(talon.getEncPosition() / CountsPerDegree, 360);
        return Utilities.wrapToRange((talon.getPosition() + offset) / CountsPerDegree, 360);
    }
    
    public double getError()
    {
        return talon.getError();
    }
    
    public double getAnglePotAdjusted()
    {
        double invert = angleInverted ? -1 : 1;
        double position = getCurrentAngle();
        
        return Utilities.round(Utilities.wrapToRange(invert * position, -HalfCircleDegrees, HalfCircleDegrees), 2);
    }
    
    public void setOffset(double degrees)
    {        
        int absolute = talon.getPulseWidthPosition() & 0xFFF;
        double offset = Utilities.wrapToRange(absolute + degrees * CountsPerDegree, -HalfCircleCounts, HalfCircleCounts);
        
        talon.setPosition(offset);
    }

    public void resetIntegral()
    {
        talon.clearIAccum();
    }
    
    public void updatePID()
    {
        talon.setP(Preferences.getInstance().getDouble("WheelAnglePID_P", SwerveConstants.ANGLE_PID_P));
        talon.setI(Preferences.getInstance().getDouble("WheelAnglePID_I", SwerveConstants.ANGLE_PID_I));
        talon.setD(Preferences.getInstance().getDouble("WheelAnglePID_D", SwerveConstants.ANGLE_PID_D));
    }
    
    /**
     * Should the motor be driving in reverse? (180 vs 360 turning)
     * @return true if motor should be reversed
     */
    public boolean isReverseMotor()
    {
        return reverseMotor;
    }
    
    /**
     * Calculate the error from the current reading and desired position, determine if motor reversal is needed
     * @param setPoint desired position
     * @param error 
     * @return 
     */
    private double applySmartReverse(double setPoint, double error)
    {
        double reversedMotorError = Utilities.wrapToRange(setPoint + HalfCircleCounts, -HalfCircleCounts, HalfCircleCounts);
        
        reverseMotor = Math.abs(reversedMotorError) < Math.abs(error);

        return (reverseMotor) ? Utilities.wrapToRange(setPoint + HalfCircleCounts, -HalfCircleCounts, HalfCircleCounts) : setPoint;
    }
}