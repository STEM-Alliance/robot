package frc.robot.reuse.hardware.sensors;

public interface Gyro
{
    public double getAngle();
    public double getPitch();
    public void zeroPitch();
    public boolean isOk();
    public void zeroYaw();
    public void zeroYaw(double robotRelativeAngleAsZero);
}
