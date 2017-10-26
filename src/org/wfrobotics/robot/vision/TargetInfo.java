package org.wfrobotics.robot.vision;

/** Rectangle the camera detected **/
public class TargetInfo
{
    public final double x;
    public final double y;
    public final double w;
    public final double h;

    public TargetInfo(double x, double y, double w, double h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public String toString()
    {
        return String.format("%.2f,%.2f,%.2f,%.2f", x, y, w, h);
    }
}
