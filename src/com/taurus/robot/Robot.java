
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

public class Robot extends SampleRobot 
{
    public enum AUTO_COMMAND
    {
        NONE,
        DROP_ARMS_F,
        DROP_ARMS_B,
        LOWBAR_CROSS,
        LOWBAR_SHOOT,
        PORTCULLIS_CROSS,
        PORTCULLIS_SHOOT;
        
        public Command getCommand(AutoTurn.STATE_TURN position)
        {
            Command autonomousCommand;
            
            switch(this)
            {
            case NONE:
                autonomousCommand = new AutoNone();
                break;
            case DROP_ARMS_F:
                autonomousCommand = new AutoReachDropArms(true);
                break;
            case DROP_ARMS_B:
                autonomousCommand = new AutoReachDropArms(false);
                break;
            case LOWBAR_CROSS:
                autonomousCommand = new AutoLowBar(false);
                break;
            case LOWBAR_SHOOT:
                autonomousCommand = new AutoLowBar(true);
                break;
            case PORTCULLIS_CROSS:
                autonomousCommand = new AutoPortCullis(position, false);
                break;
            case PORTCULLIS_SHOOT:
                autonomousCommand = new AutoPortCullis(position, true);
                break;
            default:
                autonomousCommand = new AutoNone();
                break;
            }
            
            return autonomousCommand;
        }
    }

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
    SendableChooser autoChooser;
    SendableChooser positionChooser;
    

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
        autoChooser = new SendableChooser();
        autoChooser.addDefault("Auto None", AUTO_COMMAND.NONE);
        autoChooser.addObject("Auto Drop Arms Fwd", AUTO_COMMAND.DROP_ARMS_F);
        autoChooser.addObject("Auto Drop Arms Back", AUTO_COMMAND.DROP_ARMS_B);
        autoChooser.addObject("Auto Low Bar Cross", AUTO_COMMAND.LOWBAR_CROSS);
        autoChooser.addObject("Auto Low Bar Shoot", AUTO_COMMAND.LOWBAR_SHOOT);
        autoChooser.addObject("Auto Port Cullis Cross", AUTO_COMMAND.PORTCULLIS_CROSS);
        autoChooser.addObject("Auto Port Cullis Shoot", AUTO_COMMAND.PORTCULLIS_SHOOT);
        SmartDashboard.putData("Auto mode", autoChooser);
        
        positionChooser = new SendableChooser();
        positionChooser.addDefault("None", AutoTurn.STATE_TURN.POSITION_FOUR);
        positionChooser.addObject("Position One", AutoTurn.STATE_TURN.POSITION_ONE);
        positionChooser.addObject("Position Two", AutoTurn.STATE_TURN.POSITION_TWO);
        positionChooser.addObject("Position Three", AutoTurn.STATE_TURN.POSITION_THREE);
        positionChooser.addObject("Position Four", AutoTurn.STATE_TURN.POSITION_FOUR);
        positionChooser.addObject("Position Five", AutoTurn.STATE_TURN.POSITION_FIVE);
        SmartDashboard.putData("Position", positionChooser);

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
        //autonomousCommand = (Command) autoChooser.getSelected();
        AUTO_COMMAND command =  (AUTO_COMMAND) autoChooser.getSelected();
        AutoTurn.STATE_TURN position = (AutoTurn.STATE_TURN) positionChooser.getSelected();
        autonomousCommand = command.getCommand(position);
        
        // schedule the autonomous command (example)
        if (autonomousCommand != null) autonomousCommand.start();
        
        while (isAutonomous() && isEnabled())
        {
            Scheduler.getInstance().run();
        }
    }
    
    public void disabled()
    {
        
        Command auto = (Command) autoChooser.getSelected();
        SmartDashboard.putString("Current Auto", auto.getName());
        
        double startTime = Timer.getFPGATimestamp();
        boolean cameraFixNeeded = true;
        boolean cameraFixed = false;
        
        while (isDisabled())
        {
            rockerDriveSubsystem.printSensors();
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
