package com.taurus.hardware;


import com.kauailabs.navx.frc.AHRS;
import com.taurus.Utilities;

public class Gyro extends AHRS {
    
    private double customYaw;

    public Gyro(edu.wpi.first.wpilibj.SerialPort.Port kmxp)
    {
        super(kmxp);
        customYaw = 0.0f;
    }
    
    public Gyro(edu.wpi.first.wpilibj.SPI.Port kmxp)
    {
        super(kmxp);
        customYaw = 0.0f;
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
        customYaw = angle;
        super.zeroYaw();
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
        float yaw = (float)Utilities.wrapToRange(super.getYaw() + customYaw,
                -180, 180);
        return yaw;
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
        customYaw = 0.0f;
        super.zeroYaw();
    }
}
