package com.taurus.robotspecific2015;

import com.taurus.Application;
import com.taurus.Utilities;
import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionApplication extends Application {

    Vision vision = new Vision();
    
    public VisionApplication()
    {
        super();
        
        vision.Start();
    }
    
    @Override
    public void TeleopInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TeleopPeriodicRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TeleopDeInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void AutonomousInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void AutonomousPeriodicRobotSpecific()
    {
        double maxSlide = prefs.getDouble("MaxSlide", 1), 
                slideP = prefs.getDouble("SlideP", 1), 
                targetX = prefs.getDouble("SlideTargetX", .5), 
                forwardSpeed =  prefs.getDouble("AutoSpeed", .5);
        SwerveVector Velocity = new SwerveVector(
                Utilities.clampToRange((vision.getResultX() - targetX) * slideP, -maxSlide, maxSlide), 
                forwardSpeed);
        
        double Rotation = 0;
        double Heading = 0;
        
        SmartDashboard.putNumber("Velocity X", Velocity.getX());
        SmartDashboard.putNumber("Velocity Y", Velocity.getY());
        
        drive.UpdateDrive(Velocity, Rotation, Heading);

    }

    @Override
    public void AutonomousDeInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TestModeInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TestModePeriodicRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void TestModeDeInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void DisabledInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void DisabledPeriodicRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void DisabledDeInitRobotSpecific()
    {
        // TODO Auto-generated method stub

    }

}
