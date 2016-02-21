package com.taurus.subsystems;

import com.taurus.PIDController;
import com.taurus.commands.DriveTankWithXbox;
import com.taurus.hardware.Gyro;
import com.taurus.robot.RobotMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDeviceStatus;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RockerDriveSubsystem extends Subsystem 
{
    private final double DEADBAND = .2;
    
    private double motorsPID_P, motorsPID_I, motorsPID_D;

    // order is {Front, Middle, Back}
    //          {bogie, bogie, fixed}
    private CANTalon motorsL[] = new CANTalon[RobotMap.PIN_ROCKER_TALONS_LEFT.length];
    private CANTalon motorsR[] = new CANTalon[RobotMap.PIN_ROCKER_TALONS_RIGHT.length];
    private SerialPort serial_port;
    //private RockerIMU gyro;
    
    /**
     * Constructor
     */
    public RockerDriveSubsystem()
    {
        boolean Encoder = Preferences.getInstance().getBoolean("DriveEncoders", false);
        
        // set up left side motors
        for (int i = 0; i < motorsL.length; i++)
        {
            motorsL[i] = new CANTalon(RobotMap.PIN_ROCKER_TALONS_LEFT[i]);

            
            if(Encoder && motorsL[i].isSensorPresent(FeedbackDevice.CtreMagEncoder_Relative)
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

            // since the right side rotation is inverted from the left, set that in the controller
            motorsR[i].setInverted(true);
            
            if(Encoder && motorsR[i].isSensorPresent(FeedbackDevice.CtreMagEncoder_Relative)
                    == FeedbackDeviceStatus.FeedbackStatusPresent)
            {
                // we have a sensor connected, so use it and setup speed control
                motorsR[i].setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
                motorsR[i].changeControlMode(TalonControlMode.Speed);
            }
        }
        

        //serial_port = new SerialPort(57600, SerialPort.Port.kMXP);
        //byte update_rate_hz = 100;
//        gyro = new RockerIMU(serial_port, update_rate_hz);
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
     * @param enables traction control
     */
    public void tankDrive(double right, double left, boolean tractionControlEnabled)
    {
        right = scaleForDeadband(right);
        left = scaleForDeadband(left);
        
        driveRaw(right,left, tractionControlEnabled);
    }

    /**
     * Raw drive, controlling each wheel separately
     * @param right value, -1 to 1
     * @param left value, -1 to 1
     * @param enables traction control
     */
    public void driveRaw(double right, double left, boolean tractionControlEnabled)
    {
        driveRaw(new double[]{right,right,right}, new double[]{left,left,left}, tractionControlEnabled);
    }
    /**
     * Raw drive, controlling each wheel separately
     * @param right array of values, -1 to 1
     * @param left array of values, -1 to 1
     * @param enables traction control
     */
    public void driveRaw(double[] right, double[] left, boolean tractionControlEnabled)
    {
        // Error check parameters
        if (right.length != motorsR.length || left.length != motorsL.length)
        {
            return;
        }
        
        updatePID();
        
        SmartDashboard.putBoolean("Traction", tractionControlEnabled);
        
        if (false)
        {
            double [] leftScale = motorSlipScale(motorsL);
            double [] rightScale = motorSlipScale(motorsR);
            
            //set motors to new scaled values
            for(int index = 0; index < right.length; index++){
                right[index] = right[index] * rightScale[index];
            }
            
            for(int index = 0; index < left.length; index++){
                left[index] = left[index] * leftScale[index];
            }
            
            //TODO convert values to RPM, and adjust for wheel to sensor ratio
        }
        
        for (int i = 0; i < motorsR.length; i++)
        {
            motorsR[i].set(right[i]);
        }
        for (int i = 0; i < motorsL.length; i++)
        {
            motorsL[i].set(left[i]);   
        }
    }
    
    /**
     * Driving through arcade style drive
     * @param throttle
     * @param turn
     * @param enables traction control
     */
    public void arcadeDrive(double throttle, double turn, boolean tractionControlEnabled)
    {
        double left = 0;
        double right = 0;

        throttle = scaleForDeadband(throttle);
        turn = scaleForDeadband(turn);
        
        left = throttle + turn;
        right = throttle - turn;
        
        left = limit(left);
        right = limit(right);
        
        tankDrive(right, left, tractionControlEnabled);
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
    
    /**
     * Traction control. Looks are the motors of this side and determines an appropriate scaling 
     * factor to apply to each motor.
     * @return array of scaling factors to be multiplied with the speed to apply traction control
     */
    public double[] motorSlipScale(CANTalon[] motors)
    {
        double[] scalingFactors = {0, 0, 0}; // Initially set to dummy values
        int[] motorRpms = {0, 0, 0};  // Initially set to dummy values       
        double slowestSpeed = Double.MAX_VALUE;  // Large value will be overridden by at least one motor
        
        for(int index = 0; index < motors.length; index++)
        {
            // Get motor speed
            motorRpms[index] = Math.abs(motors[index].getAnalogInVelocity());  // Absolute value works with negative velocity
            
            // Is this one slowest?
            if(motorRpms[index] < slowestSpeed)
            {
                slowestSpeed = motorRpms[index];
            }
        }
        
        // Apply traction control if we are slipping and above the deadband by a safe margin.
        for(int index = 0; index < motors.length; index++)
        {
            if (motorRpms[index] / slowestSpeed > 1.3 && motorRpms[index] > 1.5 * DEADBAND)
            {
               scalingFactors[index] = 1.5 * DEADBAND;  // Set it to some slower speed, but above the deadband
            }
        }
        
        return scalingFactors;
    }
}
            
            
    

