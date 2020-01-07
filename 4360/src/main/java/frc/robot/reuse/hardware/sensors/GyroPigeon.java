package frc.robot.reuse.hardware.sensors;


import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import frc.robot.reuse.math.HerdAngle;

public class GyroPigeon implements Gyro
{
    private final PigeonIMU pigeon;

    public GyroPigeon(TalonSRX talon)
    {
        pigeon = new PigeonIMU(talon);
    }

    public double getAngle()
    {
        final double[] ypr = new double[3];
        pigeon.getYawPitchRoll(ypr);
        return new HerdAngle(-ypr[0]).getAngle();
    }

    public double getPitch()
    {
        final double[] ypr = new double[3];
        pigeon.getYawPitchRoll(ypr);
        return new HerdAngle(-ypr[1]).getAngle();
    }

    public void zeroYaw()
    {
        zeroYaw(0);
    }
    public void zeroPitch()
    {
        zeroPitch(); // this doesnt work...
    }

    public void zeroYaw(double robotRelativeAngleAsZero)
    {
        pigeon.setYaw(robotRelativeAngleAsZero, 10);
        pigeon.setFusedHeading(0.0, 10);
        pigeon.setAccumZAngle(0, 10);
    }
    public void zeroPitch(double robotRelativeAngleAsZero)
    {
        pigeon.setYaw(robotRelativeAngleAsZero, 10);
        pigeon.setFusedHeading(0.0, 10);
        pigeon.setAccumZAngle(0, 10);
    }

    public boolean isOk()
    {
        return pigeon.getState() == PigeonIMU.PigeonState.Ready;
    }
}
