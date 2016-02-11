package com.taurus.subsystems;

import com.taurus.PIDController;
import com.taurus.commands.DriveTankWithXbox;
import com.taurus.robot.RobotMap;
import com.taurus.swerve.SwerveIMU;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDeviceStatus;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.command.Subsystem;

public class RockerDriveSubsystem extends Subsystem 
{
    private final double DEADBAND = .2;

    // order is {Front, Middle, Back}
    //          {bogie, bogie, fixed}
    private CANTalon motorsL[] = new CANTalon[RobotMap.PIN_ROCKER_TALONS_LEFT.length];
    private CANTalon motorsR[] = new CANTalon[RobotMap.PIN_ROCKER_TALONS_RIGHT.length];
    
    private double motorsPID_P, motorsPID_I, motorsPID_D;

    private SerialPort serial_port;

    private RockerIMU gyro;

    private Vision vision;

    private PIDController drivePID;
    
    
    
    /**
     * Constructor
     */
    public RockerDriveSubsystem()
    {
        // set up left side motors
        for (int i = 0; i < motorsL.length; i++)
        {
            motorsL[i] = new CANTalon(RobotMap.PIN_ROCKER_TALONS_LEFT[i]);

            
            if(motorsL[i].isSensorPresent(FeedbackDevice.CtreMagEncoder_Relative)
                    == FeedbackDeviceStatus.FeedbackStatusPresent)
            {
                // we have a sensor connected, so use it and setup speed control
                motorsL[i].setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
                motorsL[i].changeControlMode(TalonControlMode.Speed);
            }
        }
        
        // setup right side motors
        for (int i = 0; i < motorsR.length; i++)
        {
            motorsR[i] = new CANTalon(RobotMap.PIN_ROCKER_TALONS_RIGHT[i]);

            // since the right side rotation is inverted from the right, set that in the controller
            motorsR[i].setInverted(true);
            
            if(motorsR[i].isSensorPresent(FeedbackDevice.CtreMagEncoder_Relative)
                    == FeedbackDeviceStatus.FeedbackStatusPresent)
            {
                // we have a sensor connected, so use it and setup speed control
                motorsR[i].setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
                motorsR[i].changeControlMode(TalonControlMode.Speed);
            }
        }
        

        serial_port = new SerialPort(57600, SerialPort.Port.kMXP);
        byte update_rate_hz = 100;
        gyro = new RockerIMU(serial_port, update_rate_hz);
        vision = Vision.getInstance();
        
        drivePID = new PIDController(1, 0, 0, 1); //TODO change max output 
    }
    
    /**
     * set the default command
     */
    public void initDefaultCommand() 
    {
        // Set the default command for a subsystem here.
        setDefaultCommand(new DriveTankWithXbox());
    }
    
    /**
     * Update the PID values if they changed from Preferences
     */
    private void updatePID()
    {
        double p = Preferences.getInstance().getDouble("RockerPID_P", .5);
        double i = Preferences.getInstance().getDouble("RockerPID_I", 0);
        double d = Preferences.getInstance().getDouble("RockerPID_D", 0);
        
        // since we use the Talon for PID, we only want to send it if 
        // the values have changed
        if(p != motorsPID_P || i != motorsPID_I || d != motorsPID_D)
        {
            motorsPID_P = p;
            motorsPID_I = i;
            motorsPID_D = d;
            
            for (int index = 0; index < motorsL.length; index++)
            {
                motorsL[index].setPID(p, i, d);
            }
        
            for (int index = 0; index < motorsR.length; index++)
            {
                motorsR[index].setPID(p, i, d);
            }
        }
    }
    
    /**
     * Basic tank drive
     * 
     * Will apply deadband to input values
     * @param right -1 to 1
     * @param left -1 to 1
     */
    public void tankDrive(double right, double left)
    {
        right = scaleForDeadband(right);
        left = scaleForDeadband(left);
        
        driveRaw(new double[]{right,right,right}, new double[]{left,left,left});
    }

    /**
     * Raw drive, controlling each wheel separately
     * @param right array of values, -1 to 1
     * @param left array of values, -1 to 1
     */
    public void driveRaw(double[] right, double[] left)
    {
        updatePID();
        
        //TODO convert values to RPM, and adjust for wheel to sensor ratio
        
        if(right.length == motorsR.length)
        {
            for (int i = 0; i < motorsR.length; i++)
            {
                motorsR[i].set(right[i]);;   
            }
        }

        if(left.length == motorsL.length)
        {
            for (int i = 0; i < motorsL.length; i++)
            {
                motorsL[i].set(left[i]);;   
            }
        }
    }
    
    /**
     * Driving through arcade style drive
     * @param throttle
     * @param turn
     */
    public void arcadeDrive(double throttle, double turn)
    {
        double left = 0;
        double right = 0;

        throttle = scaleForDeadband(throttle);
        turn = scaleForDeadband(turn);
        
        left = throttle + turn;
        right = throttle - turn;
        
        left = limit(left);
        right = limit(right);
        
        tankDrive(right, left);
    }
   
    public boolean aim(double changeInAngle) {

        double motorOutput = drivePID.update(changeInAngle);//TODO add limits for angle

        if(Math.abs(changeInAngle) <= 5){
            driveRaw(new double[]{0.0, 0.0, 0.0},new double[]{0.0, 0.0, 0.0});
            return true;
        } else {
            driveRaw(new double[]{-motorOutput, -motorOutput, -motorOutput},
                     new double[]{ motorOutput, motorOutput, motorOutput});
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
