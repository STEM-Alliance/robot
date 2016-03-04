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
    private double pitch = Double.NaN;
    private double yaw = Double.NaN;
    private double distance = Double.NaN;
    
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
        return X() - W()/2;
    }

    public double Top()
    {
        return Y() - H()/2;
    }
    
    /**
     * Get the distance to the target, based on the known dimensions of the target
     * @return distance in inches
     */
    public double DistanceToTarget()
    {
        if(Double.isNaN(distance))
        {
            // see http://www.pyimagesearch.com/2015/01/19/find-distance-camera-objectmarker-using-python-opencv/
            distance = (Constants.TargetWidthIn * Constants.FocalLengthIn) / w;
        }
        return distance;
        
    }
    
    /**
     * Get the pitch (vertical angle) from the camera to the target
     * @return angle displaced in degrees
     */
    public double Pitch()
    {
        if(Double.isNaN(pitch))
        {
            // see http://stackoverflow.com/questions/17499409/opencv-calculate-angle-between-camera-and-pixel
            double inPerPx = Constants.TargetHeightIn / h;//changed to width
            double yChange = inPerPx * (Constants.BallShotY - y); //Difference between center of image and center of target in pixels
            double angle = Math.asin(yChange / DistanceToTarget());
            
            pitch = Math.toDegrees(angle);
            
//            double inPerPx = Constants.TargetHeightIn / h;//changed to width
//            double yChange = inPerPx * (Constants.BallShotY - y); //Difference between center of image and center of target in pixels
//            double currentAngle = 20;//Robot.aimerSubsystem.getCurrentAngle(); //current angle of the shooter
//            double distance = DistanceToTarget();
//            double Y0 = 45;//Constants.TowerHeightIn - Robot.liftSubsystem.getHeightFromFloorAverage();//Tower height - lift height
//            
//            if (y > Constants.BallShotY){
//                //target below
//                double Y1 = Constants.TowerHeightIn - Robot.liftSubsystem.getHeightFromFloorAverage();           
//                double angle1 = Math.toDegrees(Math.asin(Y1 / distance));
//                pitch = currentAngle - angle1;
//            } else {
//                //target above
//                double totalAngle = Math.toDegrees(Math.asin(Y0/distance));
//                pitch = totalAngle - currentAngle;
//            }
        }
        
        return pitch;//angle displaced
    }

    /**
     * Get the yaw (horizontal angle) from the camera to the target
     * @return angle in degrees
     */
    public double Yaw()
    {
        if(Double.isNaN(yaw))
        {
            // see http://stackoverflow.com/questions/17499409/opencv-calculate-angle-between-camera-and-pixel
            double inPerPx = Constants.TargetWidthIn / w;//changed to width
            double xChange = inPerPx * (Constants.BallShotX - x); //Difference between center of image and center of target in pixels
            double angle = Math.asin(xChange / DistanceToTarget());
            
            yaw = Math.toDegrees(angle);
        }
        return yaw;
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
