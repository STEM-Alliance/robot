package com.taurus.robotspecific2015;

import com.taurus.IApplication;
import com.taurus.swerve.SwerveApplication;

public class Application extends SwerveApplication
{
    // Runs when robot is disabled
    public void disabledPeriodic()
    {
    	
    }
    
    // Runs when robot is disabled
    public void disabledInit()
    {
    	
    }
    
    // Runs when operator mode is enabled.
    public void teleopInit()
    {
    	super.teleopInit(); // Do not remove
    }

    // Runs during operator control
    public void teleopPeriodic()
    {
        super.teleopPeriodic();	// Do not remove
    }
    
    // Called at start of autonomous mode
    public void autonomousInit()
    {
    	
    }

    // Called periodically during autonomous mode
    public void autonomousPeriodic()
    {
    
    }
}
