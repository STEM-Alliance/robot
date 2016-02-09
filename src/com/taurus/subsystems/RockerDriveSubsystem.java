package com.taurus.subsystems;

import com.taurus.PIDController;
import com.taurus.commands.DriveTankWithXbox;
import com.taurus.robot.RobotMap;
import com.taurus.swerve.SwerveIMU;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.command.Subsystem;

public class RockerDriveSubsystem extends Subsystem 
{
    private final double DEADBAND = .2;

    CANTalon motorFR;
    CANTalon motorMR;
    CANTalon motorBR;

    CANTalon motorFL;
    CANTalon motorML;
    CANTalon motorBL;
    
    RockerIMU gyro;
    SerialPort serial_port;
    Vision vision;
    
    private PIDController drivePID;

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
        
        serial_port = new SerialPort(57600, SerialPort.Port.kMXP);
        byte update_rate_hz = 100;
        gyro = new RockerIMU(serial_port, update_rate_hz);
        vision = Vision.getInstance();
        
        drivePID = new PIDController(1, 0, 0, 1); //TODO change max output 
        
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
     * 
     * Will apply deadband to input values
     * @param right -1 to 1
     * @param left -1 to 1
     */
    public void tankDrive(double right, double left){
        right = scaleForDeadband(right);
        left = scaleForDeadband(left);
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
   
    public boolean aim(double changeInAngle) {

        double motorOutput = drivePID.update(changeInAngle);//TODO add limits for angle

        if(Math.abs(changeInAngle) <= 5){
            driveRaw(0, 0, 0, 0, 0, 0);
            return true;
        } else {
            driveRaw(-motorOutput, -motorOutput, -motorOutput, motorOutput, motorOutput, motorOutput);
            return false;
        }
    }
    
    /**
     * aims the shooter at the detected target 
     * @return true when aimer is at desired angle
     */
    public boolean aim(){
        return aim(vision.getTarget().Yaw());
    }
    
    public double getEncoderRotations()
    {
        return 0;  // TODO Get from the Talon SRX's, potentially average all four?
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

    /**
     * Apply a deadband.
     * 
     * The value returned is zero or scaled relative to the deadband 
     * to retain sensitivity.
     * @param value -1 to 1
     * @return value with deadband applied or scaled
     */
    private double scaleForDeadband(double value)
    {
        double abs = Math.abs(value);
        
        if (abs < DEADBAND)
        {
            value = 0;
        }
        else
        {
            value = Math.signum(value) * ((abs - DEADBAND) / (1 - DEADBAND));
        }
        
        return value;
    }
}
