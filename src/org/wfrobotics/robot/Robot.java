
package org.wfrobotics.robot;

import org.wfrobotics.commands.*;
import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.hardware.Gyro;
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
    public enum AUTO_COMMAND
    {
        NONE,
        DRIVE,
        SHOOT,
        GEAR;
        
        public Command getCommand()
        {
            Command autonomousCommand;
            
            switch(this)
            {
            case NONE:
                autonomousCommand = new AutoDrive();
                break;
            case SHOOT:
                autonomousCommand = new AutoShoot(AutoShoot.MODE_DRIVE.DEAD_RECKONING_MIDPOINT, AutoShoot.MODE_SHOOT.DEAD_RECKONING);
                break;
            case DRIVE:
                autonomousCommand = new AutoDrive(0,Constants.AUTONOMOUS_DRIVE_SPEED, 0, Constants.AUTONOMOUS_TIME_DRIVE_MODE);
                break;
            case GEAR:
                autonomousCommand = new AutoGear(autonomousStartPosition, AutoGear.MODE.DEAD_RECKONING);
                break;
            default:
                autonomousCommand = new AutoDrive();
                break;
            }
            
            return autonomousCommand;
        }
        
        public double getGryoOffset()
        {
            double startAngle;
            
            switch(this)
            {
            case SHOOT:
                startAngle = 180;
                break;
            case GEAR:
                startAngle = 90;
                break;
            default:
                startAngle = 0;
                break;
            }
            
            return startAngle;
        }
    }
    
    public enum POSITION_ROTARY {SIDE_BOILER, CENTER, SIDE_LOADING_STATION};
    
    public static Climber climberSubsystem;
    public static SwerveDriveSteamworks driveSubsystem;
    public static Intake intakeSubsystem;
    public static Shooter shooterSubsystem;
    public static OI oi;
    public static Led ledSubsystem;
    public static Auger augerSubsystem;
    public static CameraShooter targetingSubsystem;
    public static CameraGear targetGearSubsystem;
    public static DashboardView dashboardView;
    
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
        targetGearSubsystem = new CameraGear();
        augerSubsystem = new Auger();
        climberSubsystem = new Climber();
        dashboardView = new DashboardView();
        intakeSubsystem = new Intake();
        ledSubsystem = new Led();
        shooterSubsystem = new Shooter();
        targetingSubsystem = new CameraShooter();

        oi = new OI();
        autoChooser = new SendableChooser<AUTO_COMMAND>();

        autoChooser.addDefault("Auto None", AUTO_COMMAND.NONE); // TODO pick gear/shoot as the default autonomous command
        autoChooser.addObject("Auto Forward", AUTO_COMMAND.DRIVE);
        autoChooser.addObject("Auto Shoot", AUTO_COMMAND.SHOOT);
        autoChooser.addObject("Auto Gear", AUTO_COMMAND.GEAR);
        SmartDashboard.putData("Auto Mode", autoChooser);
    }

    public void operatorControl()
    {
        if (autonomousCommand != null) autonomousCommand.cancel();
        
        while (isOperatorControl() && isEnabled())
        {
            driveSubsystem.printDash();
            SmartDashboard.putNumber("Battery", DriverStation.getInstance().getBatteryVoltage());
            Scheduler.getInstance().run();
        }
    }
    
    public void autonomous()
    {
        //autonomousCommand = (Command) autoChooser.getSelected();
        AUTO_COMMAND command =  (AUTO_COMMAND) autoChooser.getSelected();
        
        autonomousCommand = command.getCommand();
        
        // Zero the Gyro based on starting orientation of the selected autonomous mode
        Gyro.getInstance().zeroYaw(command.getGryoOffset());
        Robot.driveSubsystem.setLastHeading(command.getGryoOffset());
        
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
        while (isDisabled())
        {
            autonomousStartPosition = getRotaryStartingPosition();
            disabledDoGyro();
            
            AUTO_COMMAND command =  (AUTO_COMMAND) autoChooser.getSelected();
            SmartDashboard.putNumber("StartingAngle", command.getGryoOffset());
            
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
