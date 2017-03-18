package org.wfrobotics.subsystems.drive.swerve;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WheelDriveManager 
{
    private CANTalon driveMotor;
    
    public WheelDriveManager(int talonIndex, boolean enableSpeedSensor)
    {
        driveMotor = new CANTalon(talonIndex);

        driveMotor.setVoltageRampRate(30);  // TODO DRL try setting voltage range instead so you get a step function
        //driveMotor.setCurrentLimit(5);  // TODO DRL we should try to add this back in
        driveMotor.ConfigFwdLimitSwitchNormallyOpen(true);
        driveMotor.ConfigRevLimitSwitchNormallyOpen(true);
        driveMotor.enableForwardSoftLimit(false);
        driveMotor.enableReverseSoftLimit(false);
        driveMotor.enableBrakeMode(false);
        driveMotor.configNominalOutputVoltage(SwerveWheel.MINIMUM_SPEED, -SwerveWheel.MINIMUM_SPEED);  // Hardware deadband in closed-loop modes
        //driveMotor.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_50Ms);
        //driveMotor.SetVelocityMeasurementWindow(32);
        
        {
            driveMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
            driveMotor.changeControlMode(TalonControlMode.Speed);
            driveMotor.setPID(SwerveConstants.DRIVE_PID_P,
                              SwerveConstants.DRIVE_PID_I,
                              SwerveConstants.DRIVE_PID_D,
                              SwerveConstants.DRIVE_PID_F,
                              0,
                              10,
                              0);
            driveMotor.reverseSensor(true);
        }
    }
    
    public double get()
    {
        return driveMotor.getSpeed();
    }
    
    public void set(double speed)
    {
        driveMotor.set(speed);
    }
    
    public void setBrake(boolean enable)
    {
        driveMotor.enableBrakeMode(enable);
    }
    
    public void setVoltageRampRate(double rate)
    {
        driveMotor.setVoltageRampRate(rate);
    }

    public double debugGetCloseLoopRampRate()
    {
        return driveMotor.getCloseLoopRampRate();
    }
    
    public void setProfile(int profile)
    {
        driveMotor.setProfile(profile);
    }
}