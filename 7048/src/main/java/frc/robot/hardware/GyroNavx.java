package frc.robot.hardware;

import frc.robot.math.HerdAngle;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;

// TODO should we use callback?

public class GyroNavx implements Gyro
{
    private final AHRS navxMXP;
    private double zeroAngleYaw = 0;
    private double zeroAnglePitch = 0;

    public GyroNavx()
    {
        navxMXP = new AHRS(SPI.Port.kMXP, (byte) 200); // TODO increased update rate: Is that okay?
        navxMXP.zeroYaw();
    }

    /** Reset yaw with offset. Future gyro output yaw will be relative to (yaw right now less the offset).
     *  Ex: If offset 90 degrees, use (current - 90 degrees) as "zero".
     *      Thus getYaw() will initially return (0 - (0 - 90)) = +90 degrees.
     *      This example could correct the gyro in an autonomous mode where the robot starts facing "to the right".
     */
    public void zeroYaw(double robotRelativeAngleAsZero)
    {
        zeroAngleYaw = new HerdAngle(navxMXP.getYaw() - robotRelativeAngleAsZero).getAngle();
    }

    /** Reset yaw. Future gyro output yaw will be relative to the yaw right now. */
    public void zeroYaw()
    {
        zeroAngleYaw = new HerdAngle(navxMXP.getYaw()).getAngle();
    }

    public void zeroPitch(double robotRelativeAngleAsZero)
    {
        zeroAnglePitch = new HerdAngle(navxMXP.getPitch() - robotRelativeAngleAsZero).getAngle();
    }
    public void zeroPitch()
    {
        zeroAnglePitch = new HerdAngle(navxMXP.getPitch()).getAngle();
    }
    
    /** Yaw relative to when last zeroed */
    public double getAngle()
    {
        return new HerdAngle(navxMXP.getYaw() - zeroAngleYaw).getAngle();  // Ex: If we rotated two degrees clockwise from where yaw was zeroed, yaw = ((zero + 2) - zero) = +2 degrees
    }

    public double getPitch()
    {
        return new HerdAngle(navxMXP.getPitch() - zeroAnglePitch).getAngle(); 
    }


    public boolean isOk()
    {
        return navxMXP.isConnected();
    }

    public void reset()
    {
        navxMXP.reset();
    }

    public double getRate()
    {
        return navxMXP.getRate();
    }
}
