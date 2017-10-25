package org.wfrobotics.reuse.hardware.sensors;


import org.wfrobotics.reuse.utilities.Utilities;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;

// TODO should we use pidGet for swerve heading?
// TODO should we use callback?
// TODO pull in initial zero from robot?
// TODO create child as subsystem singleton like LED?

public class Gyro
{
    private static Gyro instance = null;

    private AHRS navxMXP = null;
    private double zeroVal = 0;

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

    /** The current yaw value in degrees (-180 to 180) */
    public double getYaw()
    {
        double angle = navxMXP.getYaw() - zeroVal;
        angle = Utilities.wrapToRange(angle, -180, 180);
        return angle;
    }

    /** yaw offset as the current value, subtracted from getYaw() */
    public void zeroYaw()
    {
        zeroYaw(0);
    }

    /** yaw offset to be subtracted from values */
    public void zeroYaw(double startAngleOffset)
    {
        double angle = navxMXP.getYaw();
        angle = Utilities.wrapToRange(angle, -180, 180);
        zeroVal = angle - startAngleOffset;
    }
}
