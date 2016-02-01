package com.taurus.subsystems;

import com.taurus.commands.DriveTankWithXbox;
import com.taurus.robot.RobotMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.command.Subsystem;

public class RockerDriveSubsystem extends Subsystem 
{

    CANTalon motorFR;
    CANTalon motorMR;
    CANTalon motorBR;

    CANTalon motorFL;
    CANTalon motorML;
    CANTalon motorBL;

    public void initDefaultCommand() 
    {
        // Set the default command for a subsystem here.
        setDefaultCommand(new DriveTankWithXbox());
    }

    public RockerDriveSubsystem()
    {
        motorFR = new CANTalon(RobotMap.PIN_ROCKER_TALON_FR);
        motorMR = new CANTalon(RobotMap.PIN_ROCKER_TALON_MR);
        motorBR = new CANTalon(RobotMap.PIN_ROCKER_TALON_BR);

        motorFL = new CANTalon(RobotMap.PIN_ROCKER_TALON_FL);
        motorML = new CANTalon(RobotMap.PIN_ROCKER_TALON_ML);
        motorBL = new CANTalon(RobotMap.PIN_ROCKER_TALON_BL);

        // TODO: set up speed control with encoders
        //        motorFR.changeControlMode(TalonControlMode.Speed);
        //        motorFR.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        //        motorFL.changeControlMode(TalonControlMode.Speed);
        //        motorFL.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        //        motorMR.changeControlMode(TalonControlMode.Speed);
        //        motorMR.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        //        motorML.changeControlMode(TalonControlMode.Speed);
        //        motorML.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        //        motorBR.changeControlMode(TalonControlMode.Speed);
        //        motorBR.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        //        motorBL.changeControlMode(TalonControlMode.Speed);
        //        motorBL.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);

        // since the left side rotation is inverted from the right, set that in the controller
        motorFL.setInverted(true);
        motorML.setInverted(true);
        motorBL.setInverted(true);
    }
    
    /**
     * Basic tank drive
     * @param right
     * @param left
     */
    public void tankDrive(double right, double left){
        driveRaw(right,right,right,left,left,left);
    }

    /**
     * Raw drive, controlling each wheel separately
     * @param rightFront
     * @param rightMid
     * @param rightBack
     * @param leftFront
     * @param leftMid
     * @param leftBack
     */
    public void driveRaw(double rightFront, double rightMid, double rightBack, double leftFront, double leftMid, double leftBack)
    {
        motorBR.set(rightBack);
        motorMR.set(rightMid);
        motorFR.set(rightFront);
        
        motorBL.set(leftBack);
        motorML.set(leftMid);
        motorFL.set(leftFront);
    }
    
    /**
     * Driving through arcade style drive
     * @param throttle
     * @param turn
     */
    public void arcadeDrive(double throttle, double turn)
    {
        double left = -1;
        double right = 1;
        left = throttle + turn;
        right = throttle - turn;
        right = limit(right);
        left = limit(left);
        tankDrive(left, right);
    }
    
    /**
     * Limits value between 1 and -1
     * @param value
     * @return
     */
    private double limit(double value)
    {
        double output = value;
        
        if(value > 1)
        {
            output = 1;
        }
        else if(value < -1 )
        {
            output = -1;
        }
        return output;
    }

}
