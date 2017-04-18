package org.wfrobotics.reuse.utilities;

import org.wfrobotics.Utilities;

/**
 * This Class provides many useful algorithms for Robot Path Planning. It uses
 * optimization techniques and knowledge of Robot Motion in order to calculate
 * smooth path trajectories, if given only discrete waypoints. The Benefit of
 * these optimization algorithms are very efficient path planning that can be
 * used to Navigate in Real-time.
 * 
 * This Class uses a method of Gradient Decent, and other optimization
 * techniques to produce smooth Velocity profiles for both left and right wheels
 * of a differential drive robot.
 * 
 * This Class does not attempt to calculate quintic or cubic splines for best
 * fitting a curve. It is for this reason, the algorithm can be ran on embedded
 * devices with very quick computation times.
 * 
 * The output of this function are independent velocity profiles for the left
 * and right wheels of a differential drive chassis. The velocity profiles start
 * and end with 0 velocity and maintain smooth transitions throughout the path.
 * 
 * This algorithm is a port from a similar algorithm running on a Robot used for
 * my PhD thesis. I have not fully optimized these functions, so there is room
 * for some improvement.
 * 
 */
public class PathPlannerSwerve {

    public Path[] origPath;
    private Path[] smoothPathOrig;
    public Path[] smoothPathFinal;

    double totalTime;
    double totalDistance;
    double numFinalPoints;

    private double pathAlpha;
    private double pathBeta;
    private double pathTolerance;

    private double velocityAlpha;
    private double velocityBeta;
    private double velocityTolerance;

    public double[][] wheelPositions;

    boolean centerPathDone = false;

    /**
     * Constructor, takes a Path of Way Points defined as a double array of
     * column vectors representing the global Cartesian points of the path in
     * {x,y} coordinates. The waypoint are traveled from one point to the next
     * in sequence.
     * 
     * For example: here is a properly formated waypoint array
     * 
     * double[][] waypointPath = new double[][]{ {1, 1}, {5, 1}, {9, 12}, {12,
     * 9}, {15,6}, {15, 4} }; This path goes from {1,1} -> {5,1} -> {9,12} ->
     * {12, 9} -> {15,6} -> {15,4} The units of these coordinates are position
     * units assumed by the user (i.e inch, foot, meters)
     * 
     * @param path
     * @param wheelPositions
     */
    public PathPlannerSwerve(Path[] originalPath,
            double[][] wheelPositions)
    {
        centerPathDone = false;
        origPath = originalPath.clone();

        // default values DO NOT MODIFY;
        setPathAlpha(.7);
        setPathBeta(0.3);
        setPathTolerance(0.0000001);

        setVelocityAlpha(0.1);
        setVelocityBeta(0.3);
        setVelocityTolerance(0.0000001);

        this.wheelPositions = Utilities.arrayCopy(wheelPositions);
    }

    public static void print(double[] path)
    {
        System.out.println("X: \t Y:");

        for (double u : path)
            System.out.println(u);
    }

    /**
     * Prints Cartesian Coordinates to the System Output as Column Vectors in
     * the Form X Y
     * 
     * @param path
     */
    public static void print(double[][] path)
    {
        System.out.println("X: \t Y:");

        for (double[] u : path)
            System.out.println(u[0] + "\t" + u[1]);
    }

    /**
     * Method upsamples the Path by linear injection. The result providing more
     * waypoints along the path.
     * 
     * BigO: Order N * injection#
     * 
     * @param orig
     * @param numToInject
     * @return
     */
    protected Path[] inject(Path[] orig, int numToInject)
    {
        Path[] expandedPath = new Path[orig.length + ((numToInject) * (orig.length - 1))];
        
        int index = 0;

        // loop through original array
        for (int i = 0; i < orig.length - 1; i++)
        {
            // copy first
            expandedPath[index] = Path.Copy(orig[i]);
            index++;

            for (int j = 1; j < numToInject + 1; j++)
            {
                // calculate intermediate x points between j and j+1 original
                // points
                double x = j * ((orig[i + 1].x - orig[i].x) / (numToInject + 1))
                        + orig[i].x;

                double y = j * ((orig[i + 1].y - orig[i].y) / (numToInject + 1))
                                        + orig[i].y;
                
                double heading = j * (Utilities.wrapToRange((orig[i+1].heading-orig[i].heading),-180,180) / (numToInject + 1))
                                + orig[i].heading;
                
                
                expandedPath[index] = Path.Init(x, y, Utilities.wrapToRange(heading, -180, 180));

                index++;
            }
            
        }
        
        // copy last
        expandedPath[index] = Path.Init(orig[orig.length-1].x, orig[orig.length-1].y, orig[orig.length-1].heading);
        index++;
        
//        for (Path path : expandedPath) {
//            System.out.println(path.x + "," + path.y + "," + path.heading);
//        }
        
        return expandedPath;
    }

    
    protected Path[] smoother(Path[] path, double weight_data,
            double weight_smooth, double tolerance)
    {
        // copy array
        Path[] newPath = Path.Copy(path);

        double change = tolerance;
        while (change >= tolerance)
        {
            change = 0.0;
            
            for (int i = 1; i < (path.length - 1); i++)
            {
                double x = newPath[i].x;
                double y = newPath[i].y;
                //double h = newPath[i].heading;
                
                newPath[i].x += weight_data * (path[i].x - newPath[i].x)
                                + weight_smooth
                                * (newPath[i - 1].x + newPath[i + 1].x - (2.0 * newPath[i].x));
                change += Math.abs(x - newPath[i].x);
                
                newPath[i].y += weight_data * (path[i].y - newPath[i].y)
                                + weight_smooth
                                * (newPath[i - 1].y + newPath[i + 1].y - (2.0 * newPath[i].y));
                change += Math.abs(y - newPath[i].y);
            }
        }
//        
//        for(int i = 1; i<newPath.length; i++)
//        {
//            newPath[i].heading = Utilities.wrapToRange(newPath[i].heading, 0,360);
//        }
//
//        for (int i = 0; i < newPath.length; i++) {
//            System.out.println(newPath[i].x + "," + newPath[i].y + "," + newPath[i].heading + "," + path[i].x + "," + path[i].y + "," + path[i].heading);
//        }
        
        return newPath;
    }

    
    /**
     * Optimization algorithm, which optimizes the data points in path to create
     * a smooth trajectory. This optimization uses gradient descent. While
     * unlikely, it is possible for this algorithm to never converge. If this
     * happens, try increasing the tolerance level.
     * 
     * BigO: N^x, where X is the number of of times the while loop iterates
     * before tolerance is met.
     * 
     * @param path
     * @param weight_data
     * @param weight_smooth
     * @param tolerance
     * @return
     */
    protected Path[] velocitySmoother(Path[] path, double weight_data,
            double weight_smooth, double tolerance)
    {
        // copy array
        Path[] newPath = Path.Copy(path);

//        for (Path path2 : newPath) {
//            path2.velocityHeading = Utilities.wrapToRange(path2.velocityHeading, -180, 180);
//            System.out.println(path2.x + "," + path2.y + "," + path2.velocityHeading);
//        }
        
        double change = tolerance;
        while (change >= tolerance)
        {
            change = 0.0;
            
            for (int i = 1; i < (path.length - 1); i++)
            {
                double x = newPath[i].velocity;
                //double h = newPath[i].velocityHeading;
                
                newPath[i].velocity += weight_data * (path[i].velocity - newPath[i].velocity)
                                + weight_smooth
                                * (newPath[i - 1].velocity + newPath[i + 1].velocity - (2.0 * newPath[i].velocity));
                change += Math.abs(x - newPath[i].velocity);
                
                // Don't smooth velocity heading here, it will cause issues
//                newPath[i].velocityHeading += weight_data * (path[i].velocityHeading - newPath[i].velocityHeading)
//                                + weight_smooth
//                                * (newPath[i - 1].velocityHeading + newPath[i + 1].velocityHeading - (2.0 * newPath[i].velocityHeading));
//                change += Math.abs(h - newPath[i].velocityHeading);
            }
        }

//        for (Path path2 : newPath) {
//            //path2.velocityHeading = Utilities.wrapToRange(path2.velocityHeading, -180, 180);
//            System.out.println(path2.x + "," + path2.y + "," + path2.velocityHeading);
//        }
        
        return newPath;
    }
  
  /**
   * Returns Velocity as a double array. The First Column vector is time,
   * based on the time step, the second vector is the velocity magnitude.
   * 
   * BigO: order N
   * 
   * @param smoothPath
   * @param timeStep
   * @return
   */
    protected Path[] velocity(Path[] smoothPath, double timeStep)
    {
        double[] dxdt = new double[smoothPath.length];
        double[] dydt = new double[smoothPath.length];
        //Path[] copiedPath = smoothPath.clone();

        // set first instance to zero
        dxdt[0] = 0;
        dydt[0] = 0;
        smoothPath[0].velocity = 0;
        smoothPath[0].velocityHeading = Math.toDegrees(
                Math.atan2((smoothPath[1].y - smoothPath[0].y) / timeStep, 
                           (smoothPath[1].x - smoothPath[0].x) / timeStep));
        smoothPath[0].time = 0;

        for (int i = 1; i < smoothPath.length; i++)
        {
            dxdt[i] = (smoothPath[i].x - smoothPath[i - 1].x) / timeStep;
            dydt[i] = (smoothPath[i].y - smoothPath[i - 1].y) / timeStep;

            // create time vector
            smoothPath[i].time = smoothPath[i-1].time + timeStep;

            smoothPath[i].velocity = Math.sqrt(Math.pow(dxdt[i],2) + Math.pow(dydt[i],2)); 
            smoothPath[i].velocityHeading = Utilities.wrapToRange(Math.toDegrees(Math.atan2(dydt[i], dxdt[i])),-180,180); 
        }
        

        // fix error for velocity when it approaches a vertical asymptote
        for(int i = 1; i<smoothPath.length; i++)
        {   
            if((smoothPath[i].velocityHeading-smoothPath[i-1].velocityHeading)>180)
            {
                smoothPath[i].velocityHeading = -360+smoothPath[i].velocityHeading;
            }
            
            if((smoothPath[i].velocityHeading-smoothPath[i-1].velocityHeading)<-180)
            {
                smoothPath[i].velocityHeading = 360+smoothPath[i].velocityHeading;
            }
        }

        return smoothPath;
    }

    /**
     * optimize velocity by minimizing the error distance at the end of travel
     * when this function converges, the fixed velocity vector will be smooth,
     * start and end with 0 velocity, and travel the same final distance as the
     * original un-smoothed velocity profile
     * 
     * This Algorithm may never converge. If this happens, reduce tolerance.
     * 
     * @param smoothVelocity
     * @param origVelocity
     * @param tolerance
     * @return
     */
    protected Path[] velocityFix(Path[] smoothVelocity,
            Path[] origVelocity, double tolerance)
    {

        /*
         * pseudo 1. Find Error Between Original Velocity and Smooth Velocity 2.
         * Keep increasing the velocity between the first and last node of the
         * smooth Velocity by a small amount 3. Recalculate the difference, stop
         * if threshold is met or repeat step 2 until the final threshold is
         * met. 3. Return the updated smoothVelocity
         */

        // calculate error difference
        double[] difference = errorSum(origVelocity, smoothVelocity);

        // copy smooth velocity into new Vector
        Path[] fixVel = smoothVelocity.clone();

        // optimize velocity by minimizing the error distance at the end of
        // travel
        // when this converges, the fixed velocity vector will be smooth, start
        // and end with 0 velocity, and travel the same final distance as the
        // original
        // un-smoothed velocity profile
        double increase = 0.0;
        while (Math.abs(difference[difference.length - 1]) > tolerance)
        {
            increase = difference[difference.length - 1] / 1 / 50;

            for (int i = 1; i < fixVel.length - 1; i++)
            {
                fixVel[i].velocity = fixVel[i].velocity - increase;
            }

            difference = errorSum(origVelocity, fixVel);
        }

        // fixVel = smoother(fixVel, 0.001, 0.001, 0.0000001);
        return fixVel;
    }

    
    /**
     * This method calculates the integral of the Smooth Velocity term and
     * compares it to the Integral of the original velocity term. In essence we
     * are comparing the total distance by the original velocity path and the
     * smooth velocity path to ensure that as we modify the smooth Velocity it
     * still covers the same distance as was intended by the original velocity
     * path.
     * 
     * BigO: Order N
     * 
     * @param origVelocity
     * @param smoothVelocity
     * @return
     */
    private double[] errorSum(Path[] origVelocity, Path[] smoothVelocity)
    {
        // copy vectors
        double[] tempOrigDist = new double[origVelocity.length];
        double[] tempSmoothDist = new double[smoothVelocity.length];
        double[] difference = new double[smoothVelocity.length];

        double timeStep = origVelocity[1].time - origVelocity[0].time;

        // copy first elements
        tempOrigDist[0] = origVelocity[0].velocity;
        tempSmoothDist[0] = smoothVelocity[0].velocity;

        // calculate difference
        for (int i = 1; i < origVelocity.length; i++)
        {
            tempOrigDist[i] = origVelocity[i].velocity * timeStep + tempOrigDist[i - 1];
            tempSmoothDist[i] = smoothVelocity[i].velocity * timeStep + tempSmoothDist[i - 1];

            difference[i] = tempSmoothDist[i] - tempOrigDist[i];

        }

        return difference;
    }

    /**
     * This method calculates the optimal parameters for determining what amount
     * of nodes to inject into the path to meet the time restraint. This
     * approach uses an iterative process to inject and smooth, yielding more
     * desirable results for the final smooth path.
     * 
     * Big O: Constant Time
     * 
     * @param numNodeOnlyPoints
     * @param maxTimeToComplete
     * @param timeStep
     */
    protected int[] injectionCounter2Steps(double numNodeOnlyPoints,
            double maxTimeToComplete, double timeStep)
    {
        int first = 0;
        int second = 0;
        int third = 0;

        double oldPointsTotal = 0;

        numFinalPoints = 0;

        int[] ret = null;

        double totalPoints = maxTimeToComplete / timeStep;

        if (totalPoints < 100)
        {
            double pointsFirst = 0;
            double pointsTotal = 0;

            for (int i = 4; i <= 6; i++)
                for (int j = 1; j <= 8; j++)
                {
                    pointsFirst = i * (numNodeOnlyPoints - 1) + numNodeOnlyPoints;
                    pointsTotal = (j * (pointsFirst - 1) + pointsFirst);

                    if (pointsTotal <= totalPoints
                        && pointsTotal > oldPointsTotal)
                    {
                        first = i;
                        second = j;
                        numFinalPoints = pointsTotal;
                        oldPointsTotal = pointsTotal;
                    }
                }

            ret = new int[] { first, second, third };
        }
        else
        {

            double pointsFirst = 0;
            double pointsSecond = 0;
            double pointsTotal = 0;

            for (int i = 1; i <= 5; i++)
                for (int j = 1; j <= 8; j++)
                    for (int k = 1; k < 8; k++)
                    {
                        pointsFirst = i * (numNodeOnlyPoints - 1) + numNodeOnlyPoints;
                        pointsSecond = (j * (pointsFirst - 1) + pointsFirst);
                        pointsTotal = (k * (pointsSecond - 1) + pointsSecond);

                        if (pointsTotal <= totalPoints)
                        {
                            first = i;
                            second = j;
                            third = k;
                            numFinalPoints = pointsTotal;
                        }
                    }

            ret = new int[] { first, second, third };
        }

        return ret;
    }


    /**
     * Calculates the left and right wheel paths based on robot track width
     * 
     * Big O: 2N
     * 
     * @param smoothPath
     *            - center smooth path of robot
     */
//    protected void determineWheelPaths(double[][] smoothPath, double[] smoothHeading)
//    {
//
//        wheelPath = new double[wheelPositions.length][smoothPath.length][2];
//
//        double[][] gradient = new double[smoothPath.length][2];
//
//        for (int i = 0; i < smoothPath.length - 1; i++)
//        {
//            // gradient[i][1] = Math.atan2(smoothPath[i+1][1] -
//            // smoothPath[i][1],smoothPath[i+1][0] - smoothPath[i][0]);
//            gradient[i][1] = smoothHeading[i + 1];
//            gradient[i][1] = gradient[i][1] * Math.PI / 180;
//        }
//
//        gradient[gradient.length - 1][1] = gradient[gradient.length - 2][1];
//
//        for (int i = 0; i < gradient.length; i++)
//        {
//            for (int j = 0; j < wheelPositions.length; j++)
//            {
//                wheelPath[j][i][0] =(wheelPositions[j][0] * Math.cos(gradient[i][1]))
//                                    + (wheelPositions[j][1] * Math.sin(gradient[i][1]))
//                                    + smoothPath[i][0];
//                wheelPath[j][i][1] = (wheelPositions[j][0] * Math.sin(gradient[i][1]))
//                                     + (wheelPositions[j][1] * Math.cos(gradient[i][1]))
//                                     + smoothPath[i][1];
//            }
//
//            // convert to degrees 0 to 360 where 0 degrees is +X - axis,
//            // accumulated to aline with WPI sensor
//            double deg = Math.toDegrees(gradient[i][1]);
//
//            gradient[i][1] = deg;
//
//            if (i > 0)
//            {
//                if ((deg - gradient[i - 1][1]) > 180)
//                    gradient[i][1] = -360 + deg;
//
//                if ((deg - gradient[i - 1][1]) < -180)
//                    gradient[i][1] = 360 + deg;
//            }
//        }
//
//        this.heading = new double[gradient.length];
//        for (int i = 0; i < gradient.length; i++)
//        {
//            this.heading[i] = gradient[i][1];
//        }
//    }

    /**
     * Returns the first column of a 2D array of doubles
     * 
     * @param arr
     *            2D array of doubles
     * @return array of doubles representing the 1st column of the initial
     *         parameter
     */
    protected static double[] getXVector(double[][] arr)
    {
        double[] temp = new double[arr.length];

        for (int i = 0; i < temp.length; i++)
            temp[i] = arr[i][0];

        return temp;
    }

    /**
     * Returns the second column of a 2D array of doubles
     * 
     * @param arr
     *            2D array of doubles
     * @return array of doubles representing the 1st column of the initial
     *         parameter
     */
    protected static double[] getYVector(double[][] arr)
    {
        double[] temp = new double[arr.length];

        for (int i = 0; i < temp.length; i++)
            temp[i] = arr[i][1];

        return temp;
    }

    protected static double[][] transposeVector(double[][] arr)
    {
        double[][] temp = new double[arr[0].length][arr.length];

        for (int i = 0; i < temp.length; i++)
            for (int j = 0; j < temp[i].length; j++)
                temp[i][j] = arr[j][i];

        return temp;
    }

    public boolean calculate(double totalTime, double timeStep)
    {
        if (calculateCenterPath(totalTime, timeStep))
        {
            //return calculateWheelPaths(totalTime, timeStep);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * This code will calculate a smooth path based on the program parameters.
     * If the user doesn't set any parameters, the will use the defaults
     * optimized for most cases. The results will be saved into the
     * corresponding class members. The user can then access .smoothPath,
     * .leftPath, .rightPath, .smoothCenterVelocity, .smoothRightVelocity,
     * .smoothLeftVelocity as needed.
     * 
     * After calling this method, the user only needs to pass
     * .smoothRightVelocity[1], .smoothLeftVelocity[1] to the corresponding
     * speed controllers on the Robot, and step through each setPoint.
     * 
     * @param totalTime
     *            - time the user wishes to complete the path in seconds. (this
     *            is the maximum amount of time the robot is allowed to take to
     *            traverse the path.)
     * @param timeStep
     *            - the frequency at which the robot controller is running on
     *            the robot.
     */
    public boolean calculateCenterPath(double totalTime, double timeStep)
    {
        /**
         * pseudo code
         * 
         * 1. Reduce input waypoints to only essential (direction changing) node
         * points 2. Calculate how many total datapoints we need to satisfy the
         * controller for "playback" 3. Simultaneously inject and smooth the
         * path until we end up with a smooth path with required number of
         * datapoints, and which follows the waypoint path. 4. Calculate left
         * and right wheel paths by calculating parallel points at each
         * datapoint
         */

        // Figure out how many nodes to inject
        int[] inject = injectionCounter2Steps(origPath.length, totalTime, timeStep);

        // iteratively inject and smooth the path
        for (int i = 0; i < inject.length; i++)
        {
            if (i == 0)
            {
                smoothPathOrig = inject(origPath, inject[0]);
                smoothPathOrig = smoother(smoothPathOrig, getPathAlpha(), getPathBeta(), getPathTolerance());

            }
            else
            {
                smoothPathOrig = inject(smoothPathOrig, inject[i]);
                smoothPathOrig = smoother(smoothPathOrig, getPathAlpha(), getPathBeta(), getPathTolerance());
            }
        }
        
        smoothPathOrig = velocity(smoothPathOrig, timeStep);

        smoothPathFinal = smoothPathOrig.clone();

        // set final vel to zero
        smoothPathFinal[smoothPathFinal.length - 1].velocity = 0.0;
        smoothPathFinal[smoothPathFinal.length - 1].velocityHeading = smoothPathFinal[smoothPathFinal.length - 2].velocityHeading;
        
        smoothPathFinal = velocitySmoother(smoothPathFinal, getVelocityAlpha(), getVelocityBeta(),
                               getVelocityTolerance());

        smoothPathFinal = velocityFix(smoothPathFinal, smoothPathOrig, 0.0000001);

        for (Path p : smoothPathFinal) {
            p.velocityHeading = Utilities.wrapToRange(-p.velocityHeading + 90, -180, 180);
        }
        
        centerPathDone = true;
        return true;
    }

    /**
     * @return the pathAlpha
     */
    public double getPathAlpha() {
        return pathAlpha;
    }

    /**
     * High alpha == close to original path, low alpha == smoother path
     * @param pathAlpha the pathAlpha to set
     */
    public void setPathAlpha(double pathAlpha) {
        this.pathAlpha = pathAlpha;
    }

    /**
     * @return the pathBeta
     */
    public double getPathBeta() {
        return pathBeta;
    }

    /**
     * Low beta == close to original path, high beta == smoother path
     * @param pathBeta the pathBeta to set
     */
    public void setPathBeta(double pathBeta) {
        this.pathBeta = pathBeta;
    }

    /**
     * @return the pathTolerance
     */
    public double getPathTolerance() {
        return pathTolerance;
    }

    /**
     * @param pathTolerance the pathTolerance to set
     */
    public void setPathTolerance(double pathTolerance) {
        this.pathTolerance = pathTolerance;
    }

    /**
     * @return the velocityAlpha
     */
    public double getVelocityAlpha() {
        return velocityAlpha;
    }

    /**
     * @param velocityAlpha the velocityAlpha to set
     */
    public void setVelocityAlpha(double velocityAlpha) {
        this.velocityAlpha = velocityAlpha;
    }

    /**
     * @return the velocityBeta
     */
    public double getVelocityBeta() {
        return velocityBeta;
    }

    /**
     * @param velocityBeta the velocityBeta to set
     */
    public void setVelocityBeta(double velocityBeta) {
        this.velocityBeta = velocityBeta;
    }

    /**
     * @return the velocityTolerance
     */
    public double getVelocityTolerance() {
        return velocityTolerance;
    }

    /**
     * @param velocityTolerance the velocityTolerance to set
     */
    public void setVelocityTolerance(double velocityTolerance) {
        this.velocityTolerance = velocityTolerance;
    }

//    public boolean calculateWheelPaths(double totalTime, double timeStep)
//    {
//        if (centerPathDone)
//        {
//            // calculate left and right path based on center path
//            determineWheelPaths(smoothPath, smoothHeading);
//
//            origWheelVelocity = new double[wheelPositions.length][][];
//
//            for (int i = 0; i < wheelPositions.length; i++)
//            {
//                origWheelVelocity[i] = velocity(wheelPath[i], timeStep);
//            }
//
//            // copy smooth velocities into fix Velocities
//            smoothWheelVelocity = new double[wheelPositions.length][][];
//
//            for (int i = 0; i < wheelPositions.length; i++)
//            {
//                smoothWheelVelocity[i] = velocity(origWheelVelocity[i], timeStep);
//            }
//
//            // Smooth velocity with zero final V
//            for (int i = 0; i < wheelPositions.length; i++)
//            {
//                smoothWheelVelocity[i] = smoother(smoothWheelVelocity[i], velocityAlpha,
//                                velocityBeta, velocityTolerance);
//            }
//
//            // fix velocity distance error
//            for (int i = 0; i < wheelPositions.length; i++)
//            {
//                smoothWheelVelocity[i] = velocityFix(smoothWheelVelocity[i],
//                                origWheelVelocity[i], 0.0000001);
//            }
//
//            return true;
//        }
//        else
//        {
//            return false;
//        }
//    }
}
