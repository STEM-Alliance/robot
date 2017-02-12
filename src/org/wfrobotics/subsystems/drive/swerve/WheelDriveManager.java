package org.wfrobotics.subsystems.drive.swerve;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

public class WheelDriveManager 
{
    private CANTalon driveMotor;
    
    public WheelDriveManager(int talonIndex, boolean enableSpeedSensor)
    {
        driveMotor = new CANTalon(talonIndex);

        driveMotor.setVoltageRampRate(20);
        //driveMotor.setCurrentLimit(5);
        driveMotor.ConfigFwdLimitSwitchNormallyOpen(true);
        driveMotor.ConfigRevLimitSwitchNormallyOpen(true);
        driveMotor.enableForwardSoftLimit(false);
        driveMotor.enableReverseSoftLimit(false);
        driveMotor.enableBrakeMode(false);

        if(SwerveConstants.DRIVE_SPEED_SENSOR_ENABLE)
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
}