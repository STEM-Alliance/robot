package org.wfrobotics.hardware;


import org.wfrobotics.Utilities;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Gyro {

    public enum PORT
    {
        SERIAL_MXP,
        SERIAL_USB,
        I2C_MXP,
        SPI_MXP;
        
        public AHRS getGyro()
        {
            AHRS gyro;
            
            switch(this)
            {
                case SPI_MXP:
                    gyro = new AHRS(edu.wpi.first.wpilibj.SPI.Port.kMXP);
                    break;
                    
                case I2C_MXP:
                    gyro = new AHRS(edu.wpi.first.wpilibj.I2C.Port.kMXP);
                    break;
                    
                case SERIAL_USB:
                    gyro = new AHRS(edu.wpi.first.wpilibj.SerialPort.Port.kUSB);
                    break;
                    
                case SERIAL_MXP:
                default:
                    gyro = new AHRS(edu.wpi.first.wpilibj.SerialPort.Port.kMXP);
                    break;
            }
            return gyro;
        }
    }
    
    protected static Gyro instance = null;
    protected static AHRS navxMXP = null;
   
    protected static double zeroVal = 0;

    /**
     * get the default instance using the Serial Port
     * @return
     */
    public static Gyro getInstance()
    {
        return getInstance(PORT.SERIAL_MXP);
    }
    
    /**
     * get the instance. if it hasn't been created, create it on the specified port
     * @param port
     * @return
     */
    public static Gyro getInstance(PORT port)
    {
        if (instance == null)
        {
            instance = new Gyro(port);
        }
        return instance;
    }
    
    protected Gyro(PORT port)
    {
        navxMXP = port.getGyro();
        navxMXP.
        zeroYaw();
    }

    /**
     * Sets the user-specified yaw offset to a specific angle.
     * 
     * Use 90 for starting facing to the right, use -90 for starting facing the left.
     * 
     * This user-specified yaw offset is automatically added to
     * subsequent yaw values reported by the getYaw() method.
     *
     * @param angle
     */
    public void setZero(double angle)
    {
        //navxMXP.setAngleAdjustment(angle);
        zeroVal = angle;
    }

    /**
     * Returns the current yaw value (in degrees, from -180 to 180) reported by
     * the navX IMU.
     * 
     * Note that the returned yaw value will be offset by a user-specified
     * offset value; this user-specified offset value is set by invoking the
     * zeroYaw() method or the setZerio(float) method.
     * 
     * @return The current yaw value in degrees (-180 to 180).
     */
    public float getYaw()
    {
        float angle = (float) (navxMXP.getAngle() - zeroVal);
        angle = Utilities.wrapToRange(angle, -180, 180);
        return angle;
    }
    

    /**
     * Sets the user-specified yaw offset to the current yaw value reported by
     * the nav6 IMU.
     * 
     * This user-specified yaw offset is automatically subtracted from
     * subsequent yaw values reported by the getYaw() method.
     */
    public void zeroYaw()
    {
//        navxMXP.setAngleAdjustment(0);
//        navxMXP.zeroYaw();
        double angle =  navxMXP.getAngle();
        angle = Utilities.wrapToRange(angle, -180, 180);;
        zeroVal = angle;
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
}
