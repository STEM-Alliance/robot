package org.wfrobotics.reuse.hardware.sensors;


import org.wfrobotics.reuse.utilities.Utilities;

import com.kauailabs.navx.frc.AHRS;

public class Gyro
{
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

    public static Gyro getInstance()
    {
        if (instance == null) { instance = new Gyro(PORT.SPI_MXP); }
        return instance;
    }

    protected Gyro(PORT port)
    {
        navxMXP = port.getGyro();
        navxMXP.zeroYaw();
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
     * zeroYaw() method or the setZero(float) method.
     * 
     * @return The current yaw value in degrees (-180 to 180).
     */
    public double getYaw()
    {
        double angle = navxMXP.getYaw() - zeroVal;
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
        zeroYaw(0);
    }

    /**
     * zeroYaw with an offset (compensating for the starting orientation of robot being different from 0 field relative)
     * @param startAngleOffset Field relative angle the robot is turned (range: -180 to 180)
     */
    public void zeroYaw(double startAngleOffset)
    {
        double angle = navxMXP.getYaw();
        angle = Utilities.wrapToRange(angle, -180, 180);
        zeroVal = angle - startAngleOffset;
    }
}
