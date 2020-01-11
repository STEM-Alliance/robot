package frc.robot.reuse.math;

public class HerdAngle implements WrappedAngle
{
    private final double angle;

    public HerdAngle(double angle)
    {
        double a = angle;

        a = (((a + 180) % 360) + 360) % 360 - 180;  // -180 to 180

        if (a == -0)
        {
            a = 0;
        }
        this.angle = a;
    }

    public HerdAngle(HerdAngle clone)
    {
        this(clone.angle);
    }

    public double getAngle()
    {
        return angle;
    }

    public String toString()
    {
        return String.format("%.1f\u00b0", angle);
    }

    public HerdAngle rotate(double angle)
    {
        return new HerdAngle(this.angle + angle);
    }

    public HerdAngle rotate(WrappedAngle b)
    {
        return rotate(b.getAngle());
    }

    public HerdAngle rotateReverse(WrappedAngle b)
    {
        return rotate(-b.getAngle());
    }
}
