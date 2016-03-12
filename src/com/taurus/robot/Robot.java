
package com.taurus.robot;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import com.taurus.commands.*;
import com.taurus.subsystems.*;
import com.taurus.vision.Vision;

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
    public static BackCameraSubsystem backCameraSubsystem;

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
        backCameraSubsystem = new BackCameraSubsystem();
        kickerSubsystem = new KickerSubsystem();
        
        oi = new OI();
        chooser = new SendableChooser();
        chooser.addDefault("Auto None", new AutoNone());
        chooser.addObject("Auto Drop Arms Fwd", new AutoReachDropArms());
        chooser.addObject("Auto Drop Arms Back", new AutoReachDropArmsBack());
        chooser.addObject("Auto Low Bar Cross", new AutoLowBarCross());
        chooser.addObject("Auto Low Bar Shoot", new AutoLowBarShoot());
        chooser.addObject("Auto Port Cullis Cross", new AutoPortCullisCross());
        chooser.addObject("Auto Port Cullis Shoot", new AutoPortCullisShoot());
        SmartDashboard.putData("Auto mode", chooser);

        SmartDashboard.putBoolean("TargetFound", false);
        SmartDashboard.putBoolean("TargetAimPitch", false);
        SmartDashboard.putBoolean("TargetAimYaw", false);
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
        Command auto = (Command) chooser.getSelected();
        SmartDashboard.putString("Current Auto", auto.getName());
        
        double startTime = Timer.getFPGATimestamp();
        boolean cameraFixNeeded = true;
        boolean cameraFixed = false;
        
        while (isDisabled())
        {
            liftSubsystem.printSensors();
            aimerSubsystem.updatePotOffsets();
            
            // this is some convoluted stuff
            // basically, wait until the camera has been initialized
            // then change the brightness value to make it take affect
            // then wait a few seconds
            // and set it back to the original value
            if(cameraFixed)
            {
//                if(cameraFixNeeded)
//                {
//                    if(Vision.getInstance().fixCamera(true))
//                    {
//                        cameraFixNeeded = false;
//                        startTime = Timer.getFPGATimestamp();
//                    }
//                }
//                else
//                {
//                    if(Timer.getFPGATimestamp() - startTime > 1.5)
//                    {
//                        if(Vision.getInstance().fixCamera(false))
//                        {
//                            cameraFixed = true;
//                        }
//                    }
//                }
            }
            
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
