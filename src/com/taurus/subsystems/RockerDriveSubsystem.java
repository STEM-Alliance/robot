package com.taurus.subsystems;

//import com.kauailabs.navx.frc.AHRS;
import com.taurus.PIDController;
import com.taurus.commands.DriveTankWithXbox;
import com.taurus.robot.RobotMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDeviceStatus;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RockerDriveSubsystem extends Subsystem 
{
    private final double DEADBAND = .2;
    
    private double motorsPID_P, motorsPID_I, motorsPID_D;

    // order is {Front, Middle, Back}
    //          {bogie, bogie, fixed}
    private CANTalon motorsL[] = new CANTalon[RobotMap.CAN_ROCKER_TALONS_LEFT.length];
    private CANTalon motorsR[] = new CANTalon[RobotMap.CAN_ROCKER_TALONS_RIGHT.length];
    //private AHRS navxMXP;  // Expander board, contains gyro
    
    private boolean applyGyro;
    
    /**
     * Constructor
     */
    public RockerDriveSubsystem()
    {
        boolean Encoder = Preferences.getInstance().getBoolean("DriveEncoders", false);
        
        // set up left side motors
        for (int i = 0; i < motorsL.length; i++)
        {
            motorsL[i] = new CANTalon(RobotMap.CAN_ROCKER_TALONS_LEFT[i]);

            
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
            motorsR[i] = new CANTalon(RobotMap.CAN_ROCKER_TALONS_RIGHT[i]);

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
        
        // Setup gyro
        try {
            /* Communicate w/navX MXP via the MXP SPI Bus.                                     */
            /* Alternatively:  I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB     */
            /* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
            //navxMXP = new AHRS(SPI.Port.kMXP);
        } catch (RuntimeException ex ) {
            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        }
        applyGyro = false;
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
     * Raw drive, controlling each wheel separately
     * @param right value, -1 to 1
     * @param left value, -1 to 1
     * @param enables traction control
     */
    public void driveRaw(double right, double left, boolean tractionControlEnabled)
    {
        double rThing = Math.signum(right);
        double lThing = Math.signum(left);
        driveRaw(new double[]{right, (Math.abs(right)-.1)*rThing, right}, 
                 new double[]{left,  (Math.abs(left)-.1)*lThing,  left},
                 tractionControlEnabled);
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
        if (tractionControlEnabled)
        {
            double [] leftScale = scaleForTractionControl(motorsL);
            double [] rightScale = scaleForTractionControl(motorsR);
            
            //set motors to new scaled values
            for(int index = 0; index < right.length; index++){
                right[index] = right[index] * rightScale[index];
            }
            
            for(int index = 0; index < left.length; index++){
                left[index] = left[index] * leftScale[index];
            }
            
            //TODO convert values to RPM, and adjust for wheel to sensor ratio
        }
        
        if (applyGyro)
        {
            // TODO - DRL apply the gyro, compensate for how much yaw/error from our heading
        }
        
        for (int i = 0; i < motorsR.length; i++)
        {
            right[i] = scaleForDeadband(right[i]);
            right[i] = Math.min(Math.max(right[i], -1), 1);  // ensure value between -1 and 1
            motorsR[i].set(right[i]);
        }
        for (int i = 0; i < motorsL.length; i++)
        {
            left[i] = scaleForDeadband(left[i]);
            left[i] = Math.min(Math.max(left[i], -1), 1);  // ensure value between -1 and 1
            motorsL[i].set(left[i]);   
        }
    }
    
    public double getEncoderRotations()
    {
        return 0;  // TODO Get from the Talon SRX's, potentially average all four?
    }
    
    public void setGyroMode(boolean enabled, boolean zero)
    {
        applyGyro = enabled;
        if (zero)
        {
            //navxMXP.zeroYaw();
        }
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
    private double[] scaleForTractionControl(CANTalon[] motors)
    {
        double[] scalingFactors = new double[motors.length];
        int[] motorRpms = new int[motors.length];
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
    
    /**
     * Barf all of the possible expander card values to smartdashboard. Intended for debugging only.
     */
    public void displayNavxMXPValues()
    {
//        /* Display 6-axis Processed Angle Data                                      */
//        SmartDashboard.putBoolean(  "IMU_Connected",        navxMXP.isConnected());
//        SmartDashboard.putBoolean(  "IMU_IsCalibrating",    navxMXP.isCalibrating());
//        SmartDashboard.putNumber(   "IMU_Yaw",              navxMXP.getYaw());
//        SmartDashboard.putNumber(   "IMU_Pitch",            navxMXP.getPitch());
//        SmartDashboard.putNumber(   "IMU_Roll",             navxMXP.getRoll());
//        
//        /* Display tilt-corrected, Magnetometer-based heading (requires             */
//        /* magnetometer calibration to be useful)                                   */
//        
//        SmartDashboard.putNumber(   "IMU_CompassHeading",   navxMXP.getCompassHeading());
//        
//        /* Display 9-axis Heading (requires magnetometer calibration to be useful)  */
//        SmartDashboard.putNumber(   "IMU_FusedHeading",     navxMXP.getFusedHeading());
//
//        /* These functions are compatible w/the WPI Gyro Class, providing a simple  */
//        /* path for upgrading from the Kit-of-Parts gyro to the navx MXP            */
//        
//        SmartDashboard.putNumber(   "IMU_TotalYaw",         navxMXP.getAngle());
//        SmartDashboard.putNumber(   "IMU_YawRateDPS",       navxMXP.getRate());
//
//        /* Display Processed Acceleration Data (Linear Acceleration, Motion Detect) */
//        
//        SmartDashboard.putNumber(   "IMU_Accel_X",          navxMXP.getWorldLinearAccelX());
//        SmartDashboard.putNumber(   "IMU_Accel_Y",          navxMXP.getWorldLinearAccelY());
//        SmartDashboard.putBoolean(  "IMU_IsMoving",         navxMXP.isMoving());
//        SmartDashboard.putBoolean(  "IMU_IsRotating",       navxMXP.isRotating());
//
//        /* Display estimates of velocity/displacement.  Note that these values are  */
//        /* not expected to be accurate enough for estimating robot position on a    */
//        /* FIRST FRC Robotics Field, due to accelerometer noise and the compounding */
//        /* of these errors due to single (velocity) integration and especially      */
//        /* double (displacement) integration.                                       */
//        
//        SmartDashboard.putNumber(   "Velocity_X",           navxMXP.getVelocityX());
//        SmartDashboard.putNumber(   "Velocity_Y",           navxMXP.getVelocityY());
//        SmartDashboard.putNumber(   "Displacement_X",       navxMXP.getDisplacementX());
//        SmartDashboard.putNumber(   "Displacement_Y",       navxMXP.getDisplacementY());
//        
//        /* Display Raw Gyro/Accelerometer/Magnetometer Values                       */
//        /* NOTE:  These values are not normally necessary, but are made available   */
//        /* for advanced users.  Before using this data, please consider whether     */
//        /* the processed data (see above) will suit your needs.                     */
//        
//        SmartDashboard.putNumber(   "RawGyro_X",            navxMXP.getRawGyroX());
//        SmartDashboard.putNumber(   "RawGyro_Y",            navxMXP.getRawGyroY());
//        SmartDashboard.putNumber(   "RawGyro_Z",            navxMXP.getRawGyroZ());
//        SmartDashboard.putNumber(   "RawAccel_X",           navxMXP.getRawAccelX());
//        SmartDashboard.putNumber(   "RawAccel_Y",           navxMXP.getRawAccelY());
//        SmartDashboard.putNumber(   "RawAccel_Z",           navxMXP.getRawAccelZ());
//        SmartDashboard.putNumber(   "RawMag_X",             navxMXP.getRawMagX());
//        SmartDashboard.putNumber(   "RawMag_Y",             navxMXP.getRawMagY());
//        SmartDashboard.putNumber(   "RawMag_Z",             navxMXP.getRawMagZ());
//        SmartDashboard.putNumber(   "IMU_Temp_C",           navxMXP.getTempC());
//        
//        /* Omnimount Yaw Axis Information                                           */
//        /* For more info, see http://navx-mxp.kauailabs.com/installation/omnimount  */
//        AHRS.BoardYawAxis yaw_axis = navxMXP.getBoardYawAxis();
//        SmartDashboard.putString(   "YawAxisDirection",     yaw_axis.up ? "Up" : "Down" );
//        SmartDashboard.putNumber(   "YawAxis",              yaw_axis.board_axis.getValue() );
//        
//        /* Sensor Board Information                                                 */
//        SmartDashboard.putString(   "FirmwareVersion",      navxMXP.getFirmwareVersion());
//        
//        /* Quaternion Data                                                          */
//        /* Quaternions are fascinating, and are the most compact representation of  */
//        /* orientation data.  All of the Yaw, Pitch and Roll Values can be derived  */
//        /* from the Quaternions.  If interested in motion processing, knowledge of  */
//        /* Quaternions is highly recommended.                                       */
//        SmartDashboard.putNumber(   "QuaternionW",          navxMXP.getQuaternionW());
//        SmartDashboard.putNumber(   "QuaternionX",          navxMXP.getQuaternionX());
//        SmartDashboard.putNumber(   "QuaternionY",          navxMXP.getQuaternionY());
//        SmartDashboard.putNumber(   "QuaternionZ",          navxMXP.getQuaternionZ());
//        
//        /* Sensor Data Timestamp */
//        //SmartDashboard.putNumber(   "SensorTimestamp",      navxMXP.getLastSensorTimestamp());
//        
//        /* Connectivity Debugging Support                                           */
//        SmartDashboard.putNumber(   "IMU_Byte_Count",       navxMXP.getByteCount());
//        SmartDashboard.putNumber(   "IMU_Update_Count",     navxMXP.getUpdateCount());
    }
}
            
            
    

