
package com.taurus.robot;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import com.taurus.commands.AutoDrive;
import com.taurus.commands.AutoDropArms;
import com.taurus.commands.AutoLowBar;
import com.taurus.commands.AutoLowBarCross;
import com.taurus.subsystems.*;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends SampleRobot {

	public static RockerDriveSubsystem rockerDriveSubsystem;
	public static ShooterSubsystem shooterSubsystem;
	public static LiftSubsystem liftSubsystem;
	public static AimerSubsystem aimerSubsystem;
	public static ManipulatorSubsystem manipulatorSubsystem;
    public static KickerSubsystem kickerSubsystem ;
	public static OI oi;
	public static BallReleaseSubsystem ballReleaseSubsystem;
    public static CameraSubsystem cameraSubsystem;

    Command autonomousCommand;
    SendableChooser chooser;
    

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        liftSubsystem = new LiftSubsystem();
        shooterSubsystem = new ShooterSubsystem();
        rockerDriveSubsystem = new RockerDriveSubsystem();
        aimerSubsystem = new AimerSubsystem();
        ballReleaseSubsystem = new BallReleaseSubsystem();
        manipulatorSubsystem = new ManipulatorSubsystem();
        cameraSubsystem = new CameraSubsystem();
        kickerSubsystem = new KickerSubsystem();
        
        oi = new OI();
        chooser = new SendableChooser();
        chooser.addDefault("Auto None", new AutoDrive());
        chooser.addObject("Auto Drop Arms", new AutoDropArms());
        chooser.addObject("Auto Low Bar Cross", new AutoLowBarCross());
        chooser.addObject("Auto Low Bar Shoot", new AutoLowBar());
        SmartDashboard.putData("Auto mode", chooser);       
    }

    public void operatorControl()
    {
        if (autonomousCommand != null) autonomousCommand.cancel();
        
        while (isOperatorControl() && isEnabled())
        {
            Scheduler.getInstance().run();
        }
    }
    
    
    public void autonomous()
    {
        autonomousCommand = (Command) chooser.getSelected();
        //autonomousCommand =  new AutoDrive();
        
//        String autoSelected = SmartDashboard.getString("Auto Mode", "Auto Terrain");
//        switch(autoSelected) {
//        case "Auto Low Bar":
//            autonomousCommand = new AutoLowBar();
//            break;
//        case "Auto Terrain":
//            autonomousCommand = new AutoTerrain();
//            break;
//        default:
//            autonomousCommand = new AutoTerrain();
//            break;
//        }
        
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
            liftSubsystem.printSensors();
            aimerSubsystem.updatePotOffsets();
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
