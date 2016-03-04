package com.taurus.demosingleswerve;

import com.taurus.controller.SwerveJoysticks;
import com.taurus.controller.SwerveXbox;
import com.taurus.swerve.SwerveVector;
import com.taurus.swerve.SwerveWheel;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Application implements com.taurus.Application {

    SwerveXbox Controller;
    //SwerveJoysticks Controller;
    SwerveWheel Wheel;
    double StartTime;
    
    public Application()
    {
        Controller = new SwerveXbox();
        //Controller = new SwerveJoysticks();
        Wheel = new SwerveWheel(0, new double[2], 0, 0, 0, 1, 2, new int[] {10, 85}, 0, Controller);
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
        SmartDashboard.putString("HaloDrive", Controller.getHaloDrive_Velocity().getMag() + " " +Controller.getHaloDrive_Velocity().getAngle() );
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
        StartTime = Timer.getFPGATimestamp(); // Set initial time
    }

    @Override
    public void AutonomousPeriodic()
    {
        // Has enough time elapsed to change direction?
        if(Timer.getFPGATimestamp() - StartTime > 5)
        {
            // Change direction
//            SwerveVector value;
//            Random randomGenerator = new Random();
//            double x;
//            double y;
//            
//            x = randomGenerator.nextDouble();
//            y = randomGenerator.nextDouble();
//            value = new SwerveVector(x, y);
//            value = SwerveVector.NewFromMagAngle(x, y*360);
//            
//            Wheel.setDesired(value, false, false);
            
            // Reset our timer
            StartTime = Timer.getFPGATimestamp();
        }
        
        Wheel.updateTask();
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