
package org.wfrobotics.robot;

import org.wfrobotics.commands.*;
import org.wfrobotics.hardware.Gyro;
import org.wfrobotics.subsystems.*;
import org.wfrobotics.subsystems.Auger;
import org.wfrobotics.vision.DashboardView;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot 
{
    public enum AUTO_COMMAND
    {
        NONE,
        GEAR;
        
        public Command getCommand()
        {
            Command autonomousCommand;
            
            switch(this)
            {
            case NONE:
                autonomousCommand = new AutoDrive();
                break;
            //case GEAR:
                //autonomousCommand = new AutoGear();  // TODO get the starting position from smart dashboard
                //break;
            default:
                autonomousCommand = new AutoDrive();
                break;
            }
            
            return autonomousCommand;
        }
    }
    
    public static Climber climberSubsystem;
    public static SwerveDriveSteamworks driveSubsystem;
    public static Intake intakeSubsystem;
    public static Shooter shooterSubsystem;
    public static OI oi;
    public static Led ledSubsystem;
    public static Auger augerSubsystem;
    public static Targeting targetingSubsystem;
    public static Aligning aligningSubsystem;
    public static DashboardView dashboardView;
    
    Command autonomousCommand;
    SendableChooser<AUTO_COMMAND> autoChooser;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() 
    {

        driveSubsystem = new SwerveDriveSteamworks();
        intakeSubsystem = new Intake();
        shooterSubsystem = new Shooter();
        climberSubsystem = new Climber();
        ledSubsystem = new Led();
        augerSubsystem = new Auger();
        targetingSubsystem = new Targeting();
        aligningSubsystem = new Aligning();
        dashboardView = new DashboardView();

        oi = new OI();
        autoChooser = new SendableChooser<AUTO_COMMAND>();
        
        autoChooser.addDefault("Auto None", AUTO_COMMAND.NONE);
        SmartDashboard.putData("Auto mode", autoChooser);
    }

    public void operatorControl()
    {
        if (autonomousCommand != null) autonomousCommand.cancel();
        
        while (isOperatorControl() && isEnabled())
        {
            SmartDashboard.putNumber("Battery", DriverStation.getInstance().getBatteryVoltage());
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
            SmartDashboard.putNumber("Battery", DriverStation.getInstance().getBatteryVoltage());
            Scheduler.getInstance().run();
        }
    }
    
    public void disabled()
    {
        while (isDisabled())
        {
            if(OI.xboxDrive.getStartButton())
                Gyro.getInstance().zeroYaw();
            
            driveSubsystem.printDash();
            SmartDashboard.putNumber("Battery", DriverStation.getInstance().getBatteryVoltage());
            
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
