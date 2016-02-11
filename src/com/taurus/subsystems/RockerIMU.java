package com.taurus.subsystems;

import com.kauailabs.nav6.frc.IMUAdvanced;
import com.taurus.Utilities;

import edu.wpi.first.wpilibj.SerialPort;

public class RockerIMU extends IMUAdvanced{
    
    private float customYaw;
    
    public RockerIMU(SerialPort serial_port, byte update_rate_hz, int yaw)
    {
        super(serial_port, update_rate_hz);
        customYaw = yaw;
    }

    public RockerIMU(SerialPort serial_port, byte update_rate_hz)
    {
        super(serial_port, update_rate_hz);
        customYaw = 0.0f;
    }
    
    public RockerIMU(SerialPort serial_port)
    {
        super(serial_port);
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
     * @param yaw
     */
    public void setZero(float yaw)
    {
        customYaw = yaw;
    }

    /**
     * Returns the current yaw value (in degrees, from -180 to 180) reported by
     * the nav6 IMU.
     * 
     * Note that the returned yaw value will be offset by a user-specified
     * offset value; this user-specified offset value is set by invoking the
     * zeroYaw() method or the setZerio(float) method.
     * 
     * @return The current yaw value in degrees (-180 to 180).
     */
    public float getYaw()
    {
        float yaw = Utilities.wrapToRange(super.getYaw() + customYaw,
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
