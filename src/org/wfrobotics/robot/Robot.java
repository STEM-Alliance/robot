package org.wfrobotics.robot;

import org.wfrobotics.hardware.Gyro;
import org.wfrobotics.hardware.led.LEDs.Color;
import org.wfrobotics.hardware.led.LEDs.Effect;
import org.wfrobotics.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.hardware.led.LEDs.LEDController;
import org.wfrobotics.hardware.led.LEDs;
import org.wfrobotics.hardware.led.MindsensorCANLight;
import org.wfrobotics.robot.Autonomous.AUTO_COMMAND;
import org.wfrobotics.robot.Autonomous.POSITION_ROTARY;
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
    public static Effect defaultLEDEffect;
    
    /**
     * This function is run when the robot is first started up and should be used for any initialization code
     */
    public void robotInit() 
    {
        Color[] defaultColors = {LEDs.GREEN, LEDs.YELLOW, LEDs.GREEN, LEDs.WHITE};
        
        driveSubsystem = new SwerveDriveSteamworks();
        augerSubsystem = new Auger();
        targetGearSubsystem = new CameraGear();
        targetShooterSubsystem = new CameraShooter();
        climberSubsystem = new Climber();
        dashboardView = new DashboardView();
        intakeSubsystem = new Intake();
        lifterSubsystem = new Lifter(true);
        shooterSubsystem = new Shooter();
        leds = new MindsensorCANLight(RobotMap.CAN_LIGHT[0]);

        oi = new OI();  // IMPORTANT: Initialize OI after subsystems, so all subsystem parameters passed to commands are initialized
        
        autoChooser = new SendableChooser<AUTO_COMMAND>();

        autoChooser.addDefault("Auto None", AUTO_COMMAND.NONE); // TODO pick gear/shoot as the default autonomous command
        autoChooser.addObject("Auto Forward (LOW GEAR)", AUTO_COMMAND.DRIVE);
        autoChooser.addObject("Auto Forward  (HIGH GEAR)", AUTO_COMMAND.DRIVE_HG);
        //autoChooser.addObject("Auto Shoot (NOT WORKING YET)", AUTO_COMMAND.SHOOT);
        autoChooser.addObject("Auto Shoot then Hopper", AUTO_COMMAND.SHOOT_THEN_HOPPER);
        autoChooser.addObject("Auto Shoot then Gear", AUTO_COMMAND.SHOOT_THEN_GEAR);
        autoChooser.addObject("Auto Gear Vision", AUTO_COMMAND.GEAR_VISION);
        //autoChooser.addObject("Auto Gear Dead Reckoning", AUTO_COMMAND.GEAR_DR);
        SmartDashboard.putData("Auto Mode", autoChooser);
        
        defaultLEDEffect = new Effect(EFFECT_TYPE.FADE, defaultColors, 4);
    }

    public void operatorControl()
    {
        if (autonomousCommand != null) autonomousCommand.cancel();
        
        leds.set(defaultLEDEffect);
        
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
            autonomousStartPosition = Autonomous.getRotaryStartingPosition();
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
}
