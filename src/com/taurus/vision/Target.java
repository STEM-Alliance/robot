package com.taurus.vision;

import java.util.Comparator;

public class Target implements Comparable<Target> {

    private double x; //center pixels (all)
    private double y;//center
    private double area;
    private double h;//target
    private double w;//target
    private double orientation;
    
    public Target(double x, double y, double area, double h, double w, double orientation)
    {
        this.x = x;
        this.y = y;
        this.area = area;
        this.h = h;
        this.w = w;
        this.orientation = orientation;
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
    
    public double Orientation()
    {
        return orientation;
    }

    /**
     * Get the distance to the target, based on the known dimensions of the target
     * @return distance in inches
     */
    public double DistanceToTarget()
    {
        //TODO see http://www.pyimagesearch.com/2015/01/19/find-distance-camera-objectmarker-using-python-opencv/
        double distanceToTarget = (Constants.TargetWidthIn * Constants.FocalLengthIn) / w;
        return distanceToTarget;
        
    }
    
    /**
     * Get the pitch (vertical angle) from the camera to the target
     * @return angle in degrees
     */
    public double Pitch()
    {
        double degrees = helperDegrees(Constants.Height,y);//helper function example
        
        return degrees;
    }

    /**
     * Get the yaw (horizontal angle) from the camera to the target
     * @return angle in degrees
     */
    public double Yaw()//add helper after fixing math
    {
        //TODO see http://stackoverflow.com/questions/17499409/opencv-calculate-angle-between-camera-and-pixel
        double inPerPx = Constants.TargetHeightIn / h;
        double xChange = inPerPx * ((Constants.Width/2) - x); //Difference between center of image and center of target in pixels
        double degrees = Math.asin(xChange / DistanceToTarget()); 
        return Math.toDegrees(degrees);
       
    }
    
    public double helperDegrees (double dimension, double centerPoint){
        double inPerPx = Constants.TargetHeightIn / h; 
        double Change = inPerPx * ((dimension/2)- centerPoint); //Difference between center of image and center of target in pixels
        double degrees = Math.asin(Change / DistanceToTarget()); 
        return Math.toDegrees(degrees);
        
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
