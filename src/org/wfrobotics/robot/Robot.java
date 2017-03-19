package org.wfrobotics.robot;

import org.wfrobotics.commands.*;
import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.hardware.Gyro;
import org.wfrobotics.hardware.led.LEDs.Color;
import org.wfrobotics.hardware.led.LEDs.Effect;
import org.wfrobotics.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.hardware.led.LEDs.LEDController;
import org.wfrobotics.hardware.led.LEDs;
import org.wfrobotics.hardware.led.MindsensorCANLight;
import org.wfrobotics.subsystems.*;
import org.wfrobotics.vision.DashboardView;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot 
{
    public static final double START_ANGLE_SHOOT = 99;
    public static final double START_ANGLE_GEAR_ONLY = 90;
    
    public enum POSITION_ROTARY {SIDE_BOILER, CENTER, SIDE_LOADING_STATION};
    
    public enum AUTO_COMMAND
    {
        NONE,
        DRIVE,
        SHOOT, SHOOT_THEN_HOPPER, SHOOT_THEN_GEAR,
        GEAR_VISION, GEAR_DR, DRIVE_HG;
        
        public Command getCommand(POSITION_ROTARY startingPosition)
        {
            Command autonomousCommand;
            
            switch(this)
            {
            case SHOOT:
                autonomousCommand = new AutoShoot(AutoShoot.MODE_DRIVE.DEAD_RECKONING_MIDPOINT, AutoShoot.MODE_SHOOT.DEAD_RECKONING);
                break;
            case SHOOT_THEN_HOPPER:
                autonomousCommand = new AutoShoot();
                break;
            case SHOOT_THEN_GEAR:
                autonomousCommand = new AutoGear(startingPosition, AutoGear.MODE.VISION, true);
                break;
            case DRIVE:
                autonomousCommand = new AutoDrive(0,Constants.AUTONOMOUS_DRIVE_SPEED, 0, Constants.AUTONOMOUS_TIME_DRIVE_MODE);
                break;
            case DRIVE_HG:
                autonomousCommand = new AutoDrive(0,Constants.AUTONOMOUS_DRIVE_SPEED*.75, 0, Constants.AUTONOMOUS_TIME_DRIVE_MODE*.75);
                break;
            case GEAR_VISION:
                autonomousCommand = new AutoGear(startingPosition, AutoGear.MODE.VISION, false);
                break;
            case GEAR_DR:
                autonomousCommand = new AutoGear(startingPosition, AutoGear.MODE.DEAD_RECKONING, false);
                break;
            case NONE:
            default:
                autonomousCommand = new AutoDrive();
                break;
            }
            
            return autonomousCommand;
        }
        
        public double getGyroOffset(POSITION_ROTARY startingPosition)
        {
            int signX = (DriverStation.getInstance().getAlliance() == Alliance.Red) ? 1:-1; // X driving based on alliance for mirrored field
            
            double startAngle;
            
            switch(this)
            {
            case SHOOT:
                startAngle = 180;
                break;
            case SHOOT_THEN_HOPPER:
            case SHOOT_THEN_GEAR:
                startAngle = signX * START_ANGLE_SHOOT;
                break;
            case GEAR_DR:
//                if(startingPosition == POSITION_ROTARY.SIDE_BOILER)
//                {
//                    startAngle = signX * START_ANGLE_SHOOT;                 
//                }
//                else
//                {
//                    startAngle = START_ANGLE_GEAR_ONLY;
//                }
//                break;
            case GEAR_VISION:
                startAngle = START_ANGLE_GEAR_ONLY;
                break;
            default:
                startAngle = 0;
                break;
            }
            
            return startAngle;
        }
    } 
    
    public static SwerveDriveSteamworks driveSubsystem;
    public static Auger augerSubsystem;
    public static Climber climberSubsystem;
    public static DashboardView dashboardView;
    public static Intake intakeSubsystem;
    public static LEDController leds;
    public static OI oi;
    
    public static Lifter lifterSubsystem;
    public static Shooter shooterSubsystem;
    public static CameraShooter targetShooterSubsystem;
    public static CameraGear targetGearSubsystem;    
    
    Command autonomousCommand;
    SendableChooser<AUTO_COMMAND> autoChooser;    
    double startAngle = 0;
    static POSITION_ROTARY autonomousStartPosition;
    
    boolean gyroInitialZero = false;
    
    /**
     * This function is run when the robot is first started up and should be used for any initialization code
     */
    public void robotInit() 
    {
        driveSubsystem = new SwerveDriveSteamworks();
        augerSubsystem = new Auger();
        targetGearSubsystem = new CameraGear();
        targetShooterSubsystem = new CameraShooter();
        climberSubsystem = new Climber();
        dashboardView = new DashboardView();
        intakeSubsystem = new Intake();
        lifterSubsystem = new Lifter(true);
        shooterSubsystem = new Shooter();

        oi = new OI();
        leds = new MindsensorCANLight(RobotMap.CAN_LIGHT[0]);
        
        autoChooser = new SendableChooser<AUTO_COMMAND>();

        autoChooser.addDefault("Auto None", AUTO_COMMAND.NONE); // TODO pick gear/shoot as the default autonomous command
        autoChooser.addObject("Auto Forward (LOW GEAR)", AUTO_COMMAND.DRIVE);
        autoChooser.addObject("Auto Forward  (HIGH GEAR)", AUTO_COMMAND.DRIVE_HG);
        //autoChooser.addObject("Auto Shoot (NOT WORKING YET)", AUTO_COMMAND.SHOOT);
        autoChooser.addObject("Auto Shoot then Hopper", AUTO_COMMAND.SHOOT_THEN_HOPPER);
        autoChooser.addObject("Auto Shoot then Gear", AUTO_COMMAND.SHOOT_THEN_GEAR);
        autoChooser.addObject("Auto Gear Vision", AUTO_COMMAND.GEAR_VISION);
        autoChooser.addObject("Auto Gear Dead Reckoning", AUTO_COMMAND.GEAR_DR);
        SmartDashboard.putData("Auto Mode", autoChooser);
    }

    public void operatorControl()
    {
        Color[] colors = {LEDs.GREEN, LEDs.YELLOW, LEDs.GREEN, LEDs.WHITE};
        
        if (autonomousCommand != null) autonomousCommand.cancel();
        
        leds.set(new Effect(EFFECT_TYPE.FADE, colors, 4));
        
        while (isOperatorControl() && isEnabled())
        {
            driveSubsystem.printDash();
            SmartDashboard.putNumber("Battery", DriverStation.getInstance().getBatteryVoltage());
            Scheduler.getInstance().run();
        }
    }
    
    public void autonomous()
    {
        DriverStation.Alliance team = DriverStation.getInstance().getAlliance();   
        Color color = (team == DriverStation.Alliance.Red) ? LEDs.RED : LEDs.BLUE;
        AUTO_COMMAND command =  (AUTO_COMMAND) autoChooser.getSelected();
        
        autonomousCommand = command.getCommand(autonomousStartPosition);
        
        // Zero the Gyro based on starting orientation of the selected autonomous mode
        Gyro.getInstance().zeroYaw(command.getGyroOffset(autonomousStartPosition));
        Robot.driveSubsystem.setLastHeading(command.getGyroOffset(autonomousStartPosition));
        
        Robot.leds.set(new Effect(EFFECT_TYPE.SOLID, color, .5));
        
        // Schedule the autonomous command
        if (autonomousCommand != null) autonomousCommand.start();
        
        while (isAutonomous() && isEnabled())
        {
            driveSubsystem.printDash();
            SmartDashboard.putNumber("Battery", DriverStation.getInstance().getBatteryVoltage());
            Scheduler.getInstance().run();
        }
    }
    
    public void disabled()
    {
        Color[] colors = {LEDs.GREEN, LEDs.YELLOW, LEDs.GREEN, LEDs.WHITE};
        
        leds.set(new Effect(EFFECT_TYPE.FADE, colors, 4));
        
        while (isDisabled())
        {
            lifterSubsystem.disabled();
            autonomousStartPosition = getRotaryStartingPosition();
            disabledDoGyro();
            
            AUTO_COMMAND command =  (AUTO_COMMAND) autoChooser.getSelected();
            SmartDashboard.putNumber("StartingAngle", command.getGyroOffset(autonomousStartPosition));
            SmartDashboard.putBoolean("TeamRed", DriverStation.getInstance().getAlliance() == Alliance.Red);
            
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
    
    private void disabledDoGyro()
    {
        // It takes some time before the gyro initializes so we have to wait before we can actually zero the first time
        if(!gyroInitialZero)
        {
            if(Math.abs(Gyro.getInstance().getYaw()) > 0.1)
            {
                Gyro.getInstance().zeroYaw();
                gyroInitialZero = true;
            }
        }
        else
        {
            if(OI.xboxDrive.getStartButton())
                Gyro.getInstance().zeroYaw();
            //Gyro.getInstance().zeroYaw();
        }
    }
    
    /**
     * Saved the starting position on field for when we switch to autonomous mode
     * @return Starting position selected by panel rotary dial
     */
    private POSITION_ROTARY getRotaryStartingPosition()
    {
        int dial = OI.panel.getRotary();
        Alliance alliance = DriverStation.getInstance().getAlliance();
        String displayValue;
        POSITION_ROTARY position;
        
        if (alliance == Alliance.Blue && dial == 7 ||
            alliance == Alliance.Red && dial == 1)
        {
            position = POSITION_ROTARY.SIDE_BOILER;
            displayValue = "BOILER";
        }
        else if (alliance == Alliance.Blue && dial == 1 ||
                alliance == Alliance.Red && dial == 7)
        {
            position = POSITION_ROTARY.SIDE_LOADING_STATION;
            displayValue = "LOADING STATION";
        }
        else if (dial == 0)
        {
            position = POSITION_ROTARY.CENTER;
            displayValue = "CENTER";
        }
        else
        {
            position = POSITION_ROTARY.CENTER;
            displayValue = "(defaulting to) CENTER";
        }
        SmartDashboard.putString("Autonomous Starting Position", displayValue);
        
        return position;
    }
}
