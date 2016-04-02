
package org.wfrobotics.robot;

import org.wfrobotics.commands.*;
import org.wfrobotics.subsystems.*;
import org.wfrobotics.vision.Vision;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot 
{
    public enum AUTO_COMMAND
    {
        NONE;
        
        public Command getCommand()
        {
            Command autonomousCommand;
            
            switch(this)
            {
            case NONE:
                autonomousCommand = new AutoNone();
                break;
            
            default:
                autonomousCommand = new AutoNone();
                break;
            }
            
            return autonomousCommand;
        }
    }

	public static TankDriveSubsystem tankDriveSubsystem;
	public static OI oi;

    Command autonomousCommand;
    SendableChooser autoChooser;
    SendableChooser positionChooser;
    SendableChooser modeChooser;

    

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        tankDriveSubsystem = new TankDriveSubsystem();
        
        oi = new OI();
        autoChooser = new SendableChooser();
        
        autoChooser.addDefault("Auto None", AUTO_COMMAND.NONE);
        SmartDashboard.putData("Auto mode", autoChooser);
    }

    public void operatorControl()
    {
        if (autonomousCommand != null) autonomousCommand.cancel();
        
        while (isOperatorControl() && isEnabled())
        {
            tankDriveSubsystem.printSensors();
            Scheduler.getInstance().run();
        }
    }
    
    
    public void autonomous()
    {
        //autonomousCommand = (Command) autoChooser.getSelected();
        AUTO_COMMAND command =  (AUTO_COMMAND) autoChooser.getSelected();
        
        autonomousCommand = command.getCommand();
        
        // schedule the autonomous command (example)
        if (autonomousCommand != null) autonomousCommand.start();
        
        while (isAutonomous() && isEnabled())
        {
            Scheduler.getInstance().run();
        }
    }
    
    public void disabled()
    {
        while (isDisabled())
        {
            tankDriveSubsystem.printSensors();
            
            Scheduler.getInstance().run();
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
