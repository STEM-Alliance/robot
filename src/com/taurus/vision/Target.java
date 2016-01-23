package com.taurus.vision;

import java.util.Comparator;

public class Target implements Comparable<Target> {

    private double x;
    private double y;
    private double area;
    private double h;
    private double w;
    
    public Target(double x, double y, double area, double h, double w)
    {
        this.x = x;
        this.y = y;
        this.area = area;
        this.h = h;
        this.w = w;
    }

    public double X()
    {
        return x;
    }
    
    public double Y()
    {
        return y;
    }
    
    public double Area()
    {
        return area;
    }
    
    public double H()
    {
        return h;
    }
    
    public double W()
    {
        return w;
    }

    /**
     * Get the distance to the target, based on the known dimensions of the target
     * @return distance in inches
     */
    public double DistanceToTarget()
    {
        //TODO see http://www.pyimagesearch.com/2015/01/19/find-distance-camera-objectmarker-using-python-opencv/
        return 0;
    }
    
    /**
     * Get the pitch (vertical angle) from the camera to the target
     * @return angle in degrees
     */
    public double Pitch()
    {
        //TODO see http://stackoverflow.com/questions/17499409/opencv-calculate-angle-between-camera-and-pixel
        return 0;
    }

    /**
     * Get the yaw (horizontal angle) from the camera to the target
     * @return angle in degrees
     */
    public double Yaw()
    {
        //TODO see http://stackoverflow.com/questions/17499409/opencv-calculate-angle-between-camera-and-pixel
        return 0;
    }

    /**
     * Compare the area of a {@link Target} to another {@link Target}
     */
    public static final Comparator<Target> AreaCompare = new Comparator<Target>()
    {
        @Override
        public int compare(Target o1, Target o2)
        {
            if (o1.area > o2.area)
            {
                return 1;
            }
            else if (o1.area == o2.area)
            {
                return 0;
            }
            else
            {
                return -1;
            }
        }
        
    };
    
    /**
     * Compare the width of a {@link Target} to another {@link Target}
     */
    public static final Comparator<Target> WidthCompare = new Comparator<Target>()
    {
        @Override
        public int compare(Target o1, Target o2)
        {
            if (o1.w > o2.w)
            {
                return 1;
            }
            else if (o1.w == o2.w)
            {
                return 0;
            }
            else
            {
                return -1;
            }
        }
        
    };
    
    /**
     * Compare the width of a {@link Target} to another {@link Target}
     */
    public static final Comparator<Target> HeightCompare = new Comparator<Target>()
    {
        @Override
        public int compare(Target o1, Target o2)
        {
            if (o1.h > o2.h)
            {
                return 1;
            }
            else if (o1.h == o2.h)
            {
                return 0;
            }
            else
            {
                return -1;
            }
        }
        
    };

    /**
     * Compare the area of a {@link Target} to another {@link Target}
     */
    public int compareTo(Target o)
    {
        return AreaCompare.compare(this, o);
    }
    
}
