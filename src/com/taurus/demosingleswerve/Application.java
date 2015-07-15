package com.taurus.demosingleswerve;

import com.taurus.controller.SwerveXbox;
import com.taurus.swerve.SwerveWheel;

public class Application implements com.taurus.Application {

    SwerveXbox Controller;
    SwerveWheel Wheel;
    
    public Application()
    {
        Controller = new SwerveXbox();
        Wheel = new SwerveWheel(0, new double[2], 0, 0, 12, 0, 1, new int[] {20, 75}, 0, Controller);
    }
    
    @Override
    public void TeleopInit()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void TeleopPeriodic()
    {
        // TODO Auto-generated method stub
        // Set the wheel speed
        Wheel.setDesired(Controller.getHaloDrive_Velocity(), Controller.getHighGearEnable(), false);
    }

    @Override
    public void TeleopDeInit()
    {
       // Do nothing       
    }

    @Override
    public void AutonomousInit()
    {
        // Do nothing
    }

    @Override
    public void AutonomousPeriodic()
    {
       // Do nothing   
    }

    @Override
    public void AutonomousDeInit()
    {
       // Do nothing   
    }

    @Override
    public void TestModeInit()
    {
       // Do nothing   
    }

    @Override
    public void TestModePeriodic()
    {
       // Do nothing 
    }

    @Override
    public void TestModeDeInit()
    {
       // Do nothing 
    }

    @Override
    public void DisabledInit()
    {
       // Do nothing  
    }

    @Override
    public void DisabledPeriodic()
    {
       // Do nothing 
    }

    @Override
    public void DisabledDeInit()
    {
       // Do nothing
    }
}