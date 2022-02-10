package frc.robot.reuse.utilities;

public class CircularBuffer extends  edu.wpi.first.util.CircularBuffer
{
    int size;

    public CircularBuffer(int size)
    {
        super(size);
        this.size = size;
    }

    public CircularBuffer(int size, double fill)
    {
        this(size);
        for (int index = 0; index < size; index++)
        {
            addFirst(fill);
        }
    }

    public CircularBuffer(int size, boolean fill)
    {
        this(size);
        for (int index = 0; index < size; index++)
        {
            addFirst(fill);
        }
    }

    /** Treat boolean as 0 or 1, use with "getAverage() > <threshold>"" to check if enough samples are true */
    public void addFirst(boolean value)
    {
        super.addFirst((value) ? 1.0 : 0.0);
    }

    public double getAverage()
    {
        double sum = 0;
        for (int i = 0; i < size; i++)
        {
            sum += get(i);
        }
        return sum / size;
    }
}

