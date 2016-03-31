
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
        LOWBAR,
        PORTCULLIS,
        CHEVAL_DEFRISE,
        ROUGH_TERRAIN,
        ROCK_WALL;
        
        public Command getCommand(AutoTurn.STATE_TURN position, boolean shoot)
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
            case LOWBAR:
                autonomousCommand = new AutoLowBar(shoot);
                break;
            case PORTCULLIS:
                autonomousCommand = new AutoPortCullis(position, shoot);
                break;
            case CHEVAL_DEFRISE:
                autonomousCommand = new AutoChevalDeFrise(position, shoot);
                break;
            case ROUGH_TERRAIN:
                autonomousCommand = new AutoRoughTerrain(position, shoot);
                break;
            case ROCK_WALL:
                autonomousCommand = new AutoRockWall(position, shoot);
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
    public static IntakeSubsystem kickerSubsystem ;
    public static LEDsSubsystem ledsSubsystem ;
	public static OI oi;
	public static BallReleaseSubsystem ballReleaseSubsystem;
    public static CameraSubsystem cameraSubsystem;
    public static BackCameraSubsystem backCameraSubsystem;
    public static ClimberClawSubsystem climberClawSubsystem;

    Command autonomousCommand;
    SendableChooser autoChooser;
    SendableChooser positionChooser;
    SendableChooser modeChooser;

    

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
        kickerSubsystem = new IntakeSubsystem();
        climberClawSubsystem = new ClimberClawSubsystem();
        ledsSubsystem = new LEDsSubsystem();
        
        oi = new OI();
        autoChooser = new SendableChooser();
        autoChooser.addDefault("Auto None", AUTO_COMMAND.NONE);
        autoChooser.addObject("Auto Drop Arms (Fwd)", AUTO_COMMAND.DROP_ARMS_F);
        autoChooser.addObject("Auto Drop Arms (Back)", AUTO_COMMAND.DROP_ARMS_B);
        autoChooser.addObject("Auto Low Bar (Fwd)", AUTO_COMMAND.LOWBAR);
        autoChooser.addObject("Auto Port Cullis (Back)", AUTO_COMMAND.PORTCULLIS);
        autoChooser.addObject("Auto Cheval DeFrise (Back)", AUTO_COMMAND.CHEVAL_DEFRISE);
        autoChooser.addObject("Auto Rough Terrain (Back)", AUTO_COMMAND.ROUGH_TERRAIN);
        autoChooser.addObject("Auto Rock Wall (Back)", AUTO_COMMAND.ROCK_WALL);
        SmartDashboard.putData("Auto mode", autoChooser);
        
        positionChooser = new SendableChooser();
        positionChooser.addDefault("None", AutoTurn.STATE_TURN.POSITION_FOUR);
        positionChooser.addObject("Position One", AutoTurn.STATE_TURN.POSITION_ONE);
        positionChooser.addObject("Position Two", AutoTurn.STATE_TURN.POSITION_TWO);
        positionChooser.addObject("Position Three", AutoTurn.STATE_TURN.POSITION_THREE);
        positionChooser.addObject("Position Four", AutoTurn.STATE_TURN.POSITION_FOUR);
        positionChooser.addObject("Position Five", AutoTurn.STATE_TURN.POSITION_FIVE);
        SmartDashboard.putData("Position", positionChooser);

        //add a new mode chooser
        modeChooser = new SendableChooser();
        modeChooser.addDefault("No Shoot", false);
        modeChooser.addObject("Shoot", true);
        SmartDashboard.putData("Shoot Mode", modeChooser);
        
        
        SmartDashboard.putBoolean("TargetFound", false);
        SmartDashboard.putBoolean("TargetAimPitch", false);
        SmartDashboard.putBoolean("TargetAimYaw", false);
    }

    public void operatorControl()
    {
        if (autonomousCommand != null) autonomousCommand.cancel();
        
        while (isOperatorControl() && isEnabled())
        {
            rockerDriveSubsystem.printSensors();
            shooterSubsystem.printSensors();
            Scheduler.getInstance().run();
        }
    }
    
    
    public void autonomous()
    {
        //autonomousCommand = (Command) autoChooser.getSelected();
        AUTO_COMMAND command =  (AUTO_COMMAND) autoChooser.getSelected();
        AutoTurn.STATE_TURN position = (AutoTurn.STATE_TURN) positionChooser.getSelected();
        boolean shoot = (boolean) modeChooser.getSelected();
        autonomousCommand = command.getCommand(position, shoot);
        
        // schedule the autonomous command (example)
        if (autonomousCommand != null) autonomousCommand.start();
        
        while (isAutonomous() && isEnabled())
        {
            Scheduler.getInstance().run();
        }
    }
    
    public void disabled()
    {
        
//        Command auto = (Command) autoChooser.getSelected();
//        SmartDashboard.putString("Current Auto", auto.getName());
        
        double startTime = Timer.getFPGATimestamp();
        boolean cameraFixNeeded = true;
        boolean cameraFixed = false;
        
        while (isDisabled())
        {
            rockerDriveSubsystem.printSensors();
            liftSubsystem.printSensors();
            aimerSubsystem.updatePotOffsets();
            shooterSubsystem.printSensors();
            
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
