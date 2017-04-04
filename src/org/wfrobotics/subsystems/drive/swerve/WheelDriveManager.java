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

        driveMotor.setVoltageRampRate(30);  // TODO DRL try setting voltage range instead so you get a step function
        //driveMotor.setCurrentLimit(5);  // TODO DRL we should try to add this back in
        driveMotor.ConfigFwdLimitSwitchNormallyOpen(true);
        driveMotor.ConfigRevLimitSwitchNormallyOpen(true);
        driveMotor.enableForwardSoftLimit(false);
        driveMotor.enableReverseSoftLimit(false);
        driveMotor.enableBrakeMode(false);
        driveMotor.configNominalOutputVoltage(0, 0);  // Hardware deadband in closed-loop modes
        //driveMotor.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_50Ms);
        //driveMotor.SetVelocityMeasurementWindow(32);
//      7.8.2. Recommended Procedure
//        // TODO DRL test nondefault values (these are defaults) driveMotor.SetVelocityMeasurementPeriod(VelocityMeasurementPeriod.Period_100Ms);
//        // TODO DRL test nondefault values (these are defaults) driveMotor.SetVelocityMeasurementWindow(64);
//        15.1. How many Talons can we use?
//        16.23. How fast can I control just ONE Talon SRX?
//        16.24. Expected symptoms when there is excessive signal reflection.
//        7.6 for complete instructions on testing Slave/Follower Talons setup.
//        16.31.4. Drive (Master) Talon manually
//        16.31.5. Re-enable Closed-Loop
        
        {
            driveMotor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
            driveMotor.changeControlMode(TalonControlMode.Speed);
            driveMotor.setPID(Constants.DRIVE_P,
                              Constants.DRIVE_I,
                              Constants.DRIVE_D,
                              Constants.DRIVE_F,
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