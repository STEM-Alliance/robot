package com.taurus.pathplanning;

public class Path {

    public double time;
    public double x;
    public double y;
    public double heading;
    public double velocity;
    public double velocityHeading;

    public static Path Init(double x, double y, double heading)
    {
        return new Path(0, x, y, heading, 0, 0);
    }

    public Path(double x, double y)
    {
        this(0, x, y, 0, 0, 0);
    }
    
    public Path(double time, double x, double y)
    {
        this(time, x, y, 0, 0, 0);
    }
    
    public Path(double time,double x, double y, double heading, double velocity, double velocityHeading)
    {
        this.time = time;
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.velocity = velocity;
        this.velocityHeading = velocityHeading;
    }
    
    public static Path[] Copy(Path[] paths)
    {
        Path[] newPaths = new Path[paths.length];
        
        for (int i = 0; i < paths.length; i++) {
            newPaths[i] = Copy(paths[i]);
        }
        return newPaths;
    }
    
    public static Path Copy(Path path)
    {
        return new Path(path.time,path.x,path.y,path.heading,path.velocity,path.velocityHeading);
    }
}
