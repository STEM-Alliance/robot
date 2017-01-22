
package org.wfrobotics.robot;

import org.wfrobotics.commands.*;
import org.wfrobotics.hardware.Gyro;
import org.wfrobotics.subsystems.*;
import org.wfrobotics.subsystems.drive.DriveSubsystem;
import org.wfrobotics.subsystems.drive.MecanumDriveSubsystem;
import org.wfrobotics.subsystems.drive.TankDriveSubsystem;
import org.wfrobotics.subsystems.drive.swerve.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.command.Subsystem;
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
    
    public static DriveSubsystem driveSubsystem;
    public static Feeder feederSubsystem;
    public static IntakeSubsystem intakeSubsystem;
    public static ShooterSubsystem shooterSubsystem;
    public static OI oi;

    Command autonomousCommand;
    SendableChooser<AUTO_COMMAND> autoChooser;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        
        switch(RobotMap.DriveSystem)
        {
            case DRIVE_SWERVE:
                driveSubsystem = new SwerveDriveSubsystem();
                break;

            case DRIVE_MECANUM:
                driveSubsystem = new MecanumDriveSubsystem();
                break;
                
            case DRIVE_TANK:
            default:
                driveSubsystem = new TankDriveSubsystem();
                break;
        }
        

        intakeSubsystem = new IntakeSubsystem();
        shooterSubsystem = new ShooterSubsystem();
        
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
            //tankDriveSubsystem.printSensors();
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
            //Gyro.getInstance().displayNavxMXPValues();
            
            driveSubsystem.printDash();
            
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
