
package org.wfrobotics.robot;

import org.wfrobotics.subsystems.Led;

import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public class Robot extends SampleRobot 
{
      
    public static OI oi;
    public static Led ledSubsystem;
    
    /**
     * This function is run when the robot is first started up and should be used for any initialization code
     */
    public void robotInit() 
    {
        ledSubsystem = new Led();
    
        oi = new OI();
    }

    public void operatorControl()
    {
        while (isOperatorControl() && isEnabled())
        {
            Scheduler.getInstance().run();
        }
    }
    
    public void autonomous()
    {
        
    }
    
    public void disabled()
    {
        while (isDisabled())
        {
            
        }
    }
    
    public void test()
    {
        while (isTest() && isEnabled())
        {
            LiveWindow.run();
        }
    }
    
    

}
