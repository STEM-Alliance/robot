package com.taurus.robotspecific2015;

import com.taurus.Application;

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
        // TODO Auto-generated method stub

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
