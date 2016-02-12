package com.taurus.vision;

import java.util.Comparator;

import com.taurus.robot.Robot;

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

    public double Left()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public double Top()
    {
        // TODO Auto-generated method stub
        return 0;
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
     * @return angle displaced in degrees
     */
    public double Pitch()
    {
        
        double inPerPx = Constants.TargetHeightIn / h;//changed to width
        double yChange = inPerPx * ((Constants.Height/2) - y); //Difference between center of image and center of target in pixels
        double currentAngle = Robot.aimerSubsystem.getCurrentAngle(); //current angle of the shooter
        double distance = DistanceToTarget();
        double deltaAngle;//angle displaced
        double Y0 = Constants.TowerHeightIn - Robot.liftSubsystem.getHeightFromFloorAverage();//Tower height - lift height
        
        if (y > Constants.Height / 2){
            //target below
            double Y1 = Constants.TowerHeightIn - Robot.liftSubsystem.getHeightFromFloorAverage();           
            double angle1 = Math.toDegrees(Math.asin(Y1 / distance));
            deltaAngle = currentAngle - angle1;
        } else {
            //target above
            double totalAngle = Math.toDegrees(Math.asin(Y0/distance));
            deltaAngle = totalAngle - currentAngle;
        }
     
        return deltaAngle;//angle displaced

    }

    /**
     * Get the yaw (horizontal angle) from the camera to the target
     * @return angle in degrees
     */
    public double Yaw()
    {
        //TODO see http://stackoverflow.com/questions/17499409/opencv-calculate-angle-between-camera-and-pixel
        double inPerPx = Constants.TargetWidthIn / w;//changed to width
        double xChange = inPerPx * ((Constants.Width/2) - x); //Difference between center of image and center of target in pixels
        double angle = Math.asin(xChange / DistanceToTarget()); 
        return Math.toDegrees(angle);
        
     
       
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
