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
    private final double MINIMUM_ANGLE_ERROR = 5;
    
    private final CANTalon talon;
    /** Invert the angle motor and sensor to swap left/right */
    private final boolean angleInverted = true;
    private final String name;
    private static final boolean DEBUG = true;

    private double offset = 0;
    private boolean reverseMotor;
    
    public WheelAngleManager(String name, int talonAddress, boolean invert)
    {
        this.name = name;
        talon = new CANTalon(talonAddress);
        talon.setVoltageRampRate(30);
        talon.ConfigFwdLimitSwitchNormallyOpen(true);
        talon.ConfigRevLimitSwitchNormallyOpen(true);
        talon.enableForwardSoftLimit(false);
        talon.enableReverseSoftLimit(false);
        talon.enableBrakeMode(false);
        talon.configNominalOutputVoltage(SwerveWheel.MINIMUM_SPEED, -SwerveWheel.MINIMUM_SPEED);  // Hardware deadband in closed-loop modes
        //angleMotor.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_50Ms);
        //angleMotor.SetVelocityMeasurementWindow(32);type name = new type();
        
        talon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        talon.changeControlMode(TalonControlMode.Position);
        talon.setPID(SwerveConstants.ANGLE_PID_P, SwerveConstants.ANGLE_PID_I, SwerveConstants.ANGLE_PID_D,
                          0, 0, 10, 0);
        //talon.reverseSensor(invert);
        talon.setInverted(invert);
    }
    
    public void setPosition(double desiredDegrees)
    {
      double desiredCounts = desiredDegrees * CountsPerDegree + offset;
      double error = Utilities.wrapToRange(desiredCounts -  getCurrentAngle(), -HalfCircleCounts, HalfCircleCounts);
      double commanded;
      
//      if (error < MINIMUM_ANGLE_ERROR)
//      {   
//          commanded = talon.get();
//          //resetIntegral();  // Very close, clear accumulated (I) so we don't move
//      }
//      else
      {
          commanded = desiredCounts;//applySmartReverse(desiredCounts, error);
      }
      
      talon.set(commanded);//Pos:1580319
        
      if(DEBUG)
      {
          SmartDashboard.putNumber(name + ".get", talon.getPosition());
          SmartDashboard.putNumber(name + ".Desired", desiredDegrees);
//          SmartDashboard.putNumber(name + ".SensorValue", sensorValue);
//          SmartDashboard.putNumber(name + ".Error", error);
//          SmartDashboard.putBoolean(name + ".Reverse", reverseMotor);
          SmartDashboard.putNumber(name + ".Commanded", commanded / CountsPerDegree);
          SmartDashboard.putNumber(name + ".angle.err", talon.getClosedLoopError());
      }
    }
    
    private double getCurrentAngle()
    {
        //return Utilities.wrapToRange(talon.getEncPosition() / CountsPerDegree, 360);
        return Utilities.wrapToRange(talon.get() / CountsPerDegree, 360);
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
        double reversedMotorError = Utilities.wrapToRange(error + HalfCircleCounts, -HalfCircleCounts, HalfCircleCounts);
        
        reverseMotor = Math.abs(reversedMotorError) < Math.abs(error);

        return (reverseMotor) ? Utilities.wrapToRange(setPoint - HalfCircleCounts, -HalfCircleCounts, HalfCircleCounts) : setPoint;
    }
}