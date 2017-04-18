package org.wfrobotics.reuse.utilities;

public class CircularBuffer extends edu.wpi.first.wpilibj.CircularBuffer
{
    int size;
    
    public CircularBuffer(int size)
    {
        super(size);
        this.size = size;
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
