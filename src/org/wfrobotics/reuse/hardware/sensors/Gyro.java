package org.wfrobotics.reuse.hardware.sensors;

import org.wfrobotics.reuse.utilities.HerdAngle;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;

// TODO should we use pidGet for swerve heading?
// TODO should we use callback?

public class Gyro
{
    private static Gyro instance = null;

    private AHRS navxMXP = null;
    private double zeroAngle = 0;

    private Gyro(SPI.Port port)
    {
        navxMXP = new AHRS(port, (byte) 200); // TODO increased update rate: Is that okay?
        navxMXP.zeroYaw();
    }

    public static Gyro getInstance()
    {
        if (instance == null) { instance = new Gyro(SPI.Port.kMXP); }
        return instance;
    }

    /** Yaw relative to when last zeroed */
    public double getYaw()
    {
        return new HerdAngle(navxMXP.getYaw() - zeroAngle).getAngle();  // Ex: If we rotated two degrees clockwise from where yaw was zeroed, yaw = ((zero + 2) - zero) = +2 degrees
    }

    /** Reset yaw. Future gyro output yaw will be relative to the yaw right now. */
    public void zeroYaw()
    {
        zeroAngle = new HerdAngle(navxMXP.getYaw()).getAngle();
    }

    /** Reset yaw with offset. Future gyro output yaw will be relative to (yaw right now less the offset).
     *  Ex: If offset 90 degrees, use (current - 90 degrees) as "zero".
     *      Thus getYaw() will initially return (0 - (0 - 90)) = +90 degrees.
     *      This example could correct the gyro in an autonomous mode where the robot starts facing "to the right".
     */
    public void zeroYaw(double robotRelativeAngleAsZero)
    {
        zeroAngle = new HerdAngle(navxMXP.getYaw() - robotRelativeAngleAsZero).getAngle();
    }
}
