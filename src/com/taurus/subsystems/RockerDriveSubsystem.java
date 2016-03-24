package com.taurus.subsystems;

import com.kauailabs.navx.frc.AHRS;
import com.taurus.PIDController;
import com.taurus.Utilities;
import com.taurus.commands.DriveTankWithXbox;
import com.taurus.hardware.Gyro;
import com.taurus.robot.RobotMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDeviceStatus;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.command.PIDCommand;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RockerDriveSubsystem extends Subsystem 
{
    private final double DEADBAND = 0;

    // order is {Front, Middle, Back}
    //          {bogie, bogie, fixed}
    private CANTalon motorsL[] = new CANTalon[RobotMap.CAN_ROCKER_TALONS_LEFT.length];
    private CANTalon motorsR[] = new CANTalon[RobotMap.CAN_ROCKER_TALONS_RIGHT.length];
    private Gyro navxMXP;  // Expander board, contains gyro
    
    private boolean applyGyro;
    private double desiredHeading;
    private PIDController headingPID;
    
    private final double HEADING_TOLERANCE = 2;
    
    /**
     * Constructor
     */
    public RockerDriveSubsystem()
    {
        
        // set up left side motors
        for (int i = 0; i < motorsL.length; i++)
        {
            motorsL[i] = new CANTalon(RobotMap.CAN_ROCKER_TALONS_LEFT[i]);
        }
        
        // setup right side motors
        for (int i = 0; i < motorsR.length; i++)
        {
            motorsR[i] = new CANTalon(RobotMap.CAN_ROCKER_TALONS_RIGHT[i]);

            // since the right side rotation is inverted from the left, set that in the controller
            motorsR[i].setInverted(true);
        }
        
        // Setup gyro
        try {
            /* Communicate w/navX MXP via the MXP SPI Bus.                                     */
            /* Alternatively:  I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB     */
            /* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
            navxMXP = new Gyro(SerialPort.Port.kMXP);
        } catch (RuntimeException ex ) {
            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        }
        applyGyro = false;
        
        headingPID = new PIDController(.2, 0, 0, .5);
    }
    
    /**
     * set the default command
     */
    public void initDefaultCommand() 
    {
        // Set the default command for a subsystem here.
        setDefaultCommand(new DriveTankWithXbox(false));
    }
    
    /**
     * Update the PID values if they changed from Preferences
     */
    private void updatePID()
    {
        headingPID.setP(Preferences.getInstance().getDouble("HeadingPID_P", .2));
        headingPID.setI(Preferences.getInstance().getDouble("HeadingPID_I", 0));
        headingPID.setD(Preferences.getInstance().getDouble("HeadingPID_D", 0));
        headingPID.setMin(Preferences.getInstance().getDouble("HeadingPID_Min", .1));
    }

    /**
     * Raw drive, controlling each wheel separately
     * @param right value, -1 to 1
     * @param left value, -1 to 1
     * @param enables traction control
     */
    public void driveRaw(double right, double left)
    {
        right = scaleForDeadband(right);
        right = Math.min(Math.max(right, -1), 1);  // ensure value between -1 and 1
        
        
        for (int i = 0; i < motorsR.length; i++)
        {
            motorsR[i].set(right);
        }
        
        left = scaleForDeadband(left);
        left = Math.min(Math.max(left, -1), 1);  // ensure value between -1 and 1
        
        for (int i = 0; i < motorsL.length; i++)
        {
            motorsL[i].set(left);   
        }
        SmartDashboard.putNumber("MotorL", left);
        SmartDashboard.putNumber("MotorR", right);
        
    }
    

    /**
     * turn to an angle, offset from current heading
     * @param angle
     * @return true if done
     */
    public boolean turnToAngleOffset(double angle)
    {
        return turnToAngle(navxMXP.getYaw() + angle);
    }
    
    /**
     * turn to a desired angle
     * @param angle
     * @return true if done
     */
    public boolean turnToAngle(double angle)
    {
        return turnToAngle(angle, false, false);
    }

    
    /**
     * turn to a desired angle, with option to use only one side and only forward or backward
     * @param angle
     * @return true if done
     */
    public boolean turnToAngle(double angle, boolean singleSide, boolean forward)
    {
        double output = 0;
        double error = Utilities.wrapToRange(angle - navxMXP.getYaw(), -180, 180);
        
        updatePID();
        
        if(Math.abs(error) > HEADING_TOLERANCE)
        {
            // get the suggested motor output
            output = headingPID.update(error);
            
            // tell it to turn
            if(error < 3 && singleSide)
            {
                //TODO need brake mode enabled?
                
                // small error, so only try and turn one side
                if(output > 0)
                {
                    // spin clockwise
                    if(forward)
                        // set left side forward, not right
                        driveRaw(0,output);
                    else
                        // set right side backward, not left
                        driveRaw(-output,0);
                }
                else
                {
                    // spin counter clockwise
                    if(forward)
                        // set right side forward, not left
                        driveRaw(-output,0);
                    else
                        // set left side backward, not right
                        driveRaw(0,output);
                }
            }
            else
            {
                driveRaw(-output,output);
            }
        }
        else
        {
            driveRaw(0,0);
        }
        
        // save off angle in case you need to use it
        desiredHeading = angle;
        
        // we're done if we're at the angle, and we're no longer turning
        return Math.abs(error) < HEADING_TOLERANCE;// && Math.abs(output) < .1;
    }
    
    
    public void enableGyro(boolean enabled)
    {
        applyGyro = enabled;
    }
    
    public void zeroGyro(double angle)
    {
        navxMXP.setZero(angle);
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
     * Barf all of the possible expander card values to smartdashboard. Intended for debugging only.
     */
    public void displayNavxMXPValues()
    {
        /* Display 6-axis Processed Angle Data                                      */
        SmartDashboard.putBoolean(  "IMU_Connected",        navxMXP.isConnected());
        SmartDashboard.putBoolean(  "IMU_IsCalibrating",    navxMXP.isCalibrating());
        SmartDashboard.putNumber(   "IMU_Yaw",              navxMXP.getYaw());
        SmartDashboard.putNumber(   "IMU_Pitch",            navxMXP.getPitch());
        SmartDashboard.putNumber(   "IMU_Roll",             navxMXP.getRoll());
        
        /* Display tilt-corrected, Magnetometer-based heading (requires             */
        /* magnetometer calibration to be useful)                                   */
        
        SmartDashboard.putNumber(   "IMU_CompassHeading",   navxMXP.getCompassHeading());
        
        /* Display 9-axis Heading (requires magnetometer calibration to be useful)  */
        SmartDashboard.putNumber(   "IMU_FusedHeading",     navxMXP.getFusedHeading());

        /* These functions are compatible w/the WPI Gyro Class, providing a simple  */
        /* path for upgrading from the Kit-of-Parts gyro to the navx MXP            */
        
        SmartDashboard.putNumber(   "IMU_TotalYaw",         navxMXP.getAngle());
        SmartDashboard.putNumber(   "IMU_YawRateDPS",       navxMXP.getRate());

        /* Display Processed Acceleration Data (Linear Acceleration, Motion Detect) */
        
        SmartDashboard.putNumber(   "IMU_Accel_X",          navxMXP.getWorldLinearAccelX());
        SmartDashboard.putNumber(   "IMU_Accel_Y",          navxMXP.getWorldLinearAccelY());
        SmartDashboard.putBoolean(  "IMU_IsMoving",         navxMXP.isMoving());
        SmartDashboard.putBoolean(  "IMU_IsRotating",       navxMXP.isRotating());

        /* Display estimates of velocity/displacement.  Note that these values are  */
        /* not expected to be accurate enough for estimating robot position on a    */
        /* FIRST FRC Robotics Field, due to accelerometer noise and the compounding */
        /* of these errors due to single (velocity) integration and especially      */
        /* double (displacement) integration.                                       */
        
        SmartDashboard.putNumber(   "Velocity_X",           navxMXP.getVelocityX());
        SmartDashboard.putNumber(   "Velocity_Y",           navxMXP.getVelocityY());
        SmartDashboard.putNumber(   "Displacement_X",       navxMXP.getDisplacementX());
        SmartDashboard.putNumber(   "Displacement_Y",       navxMXP.getDisplacementY());
        
        /* Display Raw Gyro/Accelerometer/Magnetometer Values                       */
        /* NOTE:  These values are not normally necessary, but are made available   */
        /* for advanced users.  Before using this data, please consider whether     */
        /* the processed data (see above) will suit your needs.                     */
        
        SmartDashboard.putNumber(   "RawGyro_X",            navxMXP.getRawGyroX());
        SmartDashboard.putNumber(   "RawGyro_Y",            navxMXP.getRawGyroY());
        SmartDashboard.putNumber(   "RawGyro_Z",            navxMXP.getRawGyroZ());
        SmartDashboard.putNumber(   "RawAccel_X",           navxMXP.getRawAccelX());
        SmartDashboard.putNumber(   "RawAccel_Y",           navxMXP.getRawAccelY());
        SmartDashboard.putNumber(   "RawAccel_Z",           navxMXP.getRawAccelZ());
        SmartDashboard.putNumber(   "RawMag_X",             navxMXP.getRawMagX());
        SmartDashboard.putNumber(   "RawMag_Y",             navxMXP.getRawMagY());
        SmartDashboard.putNumber(   "RawMag_Z",             navxMXP.getRawMagZ());
        SmartDashboard.putNumber(   "IMU_Temp_C",           navxMXP.getTempC());
        
        /* Omnimount Yaw Axis Information                                           */
        /* For more info, see http://navx-mxp.kauailabs.com/installation/omnimount  */
        AHRS.BoardYawAxis yaw_axis = navxMXP.getBoardYawAxis();
        SmartDashboard.putString(   "YawAxisDirection",     yaw_axis.up ? "Up" : "Down" );
        SmartDashboard.putNumber(   "YawAxis",              yaw_axis.board_axis.getValue() );
        
        /* Sensor Board Information                                                 */
        SmartDashboard.putString(   "FirmwareVersion",      navxMXP.getFirmwareVersion());
        
        /* Quaternion Data                                                          */
        /* Quaternions are fascinating, and are the most compact representation of  */
        /* orientation data.  All of the Yaw, Pitch and Roll Values can be derived  */
        /* from the Quaternions.  If interested in motion processing, knowledge of  */
        /* Quaternions is highly recommended.                                       */
        SmartDashboard.putNumber(   "QuaternionW",          navxMXP.getQuaternionW());
        SmartDashboard.putNumber(   "QuaternionX",          navxMXP.getQuaternionX());
        SmartDashboard.putNumber(   "QuaternionY",          navxMXP.getQuaternionY());
        SmartDashboard.putNumber(   "QuaternionZ",          navxMXP.getQuaternionZ());
        
        /* Sensor Data Timestamp */
        //SmartDashboard.putNumber(   "SensorTimestamp",      navxMXP.getLastSensorTimestamp());
        
        /* Connectivity Debugging Support                                           */
        SmartDashboard.putNumber(   "IMU_Byte_Count",       navxMXP.getByteCount());
        SmartDashboard.putNumber(   "IMU_Update_Count",     navxMXP.getUpdateCount());
    }

    public void printSensors()
    {
        SmartDashboard.putNumber("Robot Heading", navxMXP.getYaw());
        SmartDashboard.putNumber("Desired Heading", desiredHeading);

        SmartDashboard.putString("RobotPositionInfo", navxMXP.getYaw() +"," + motorsL[0].get()  +"," + motorsR[0].get());
    }

    public double getYaw()
    {
        return navxMXP.getYaw();
    }
    
    public void zeroYaw(double angle)
    {
        navxMXP.setZero(angle);
    }
    
    public void setBrakeMode(boolean enable)
    {
        for (int index = 0; index < motorsL.length; index++)
        {
            motorsL[index].enableBrakeMode(enable);
        }
        
        for (int index = 0; index < motorsR.length; index++)
        {
            motorsR[index].enableBrakeMode(enable);
        }
    }
}
            
            
    

