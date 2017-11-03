package org.wfrobotics.robot;

import org.wfrobotics.reuse.hardware.sensors.Gyro;
import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.utilities.DashboardView;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.robot.config.Autonomous;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.VisionMode;
import org.wfrobotics.robot.subsystems.Auger;
import org.wfrobotics.robot.subsystems.Climber;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.LED;
import org.wfrobotics.robot.subsystems.Lifter;
import org.wfrobotics.robot.subsystems.Shooter;
import org.wfrobotics.robot.subsystems.SwerveDriveSteamworks;
import org.wfrobotics.robot.vision.messages.CameraMode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

public class Robot extends SampleRobot
{
    private HerdLogger log = new HerdLogger(Robot.class);
    private RobotState state = RobotState.getInstance();
    private LED leds;
    public static SwerveDriveSteamworks driveSubsystem;
    public static Auger augerSubsystem;
    public static Climber climberSubsystem;
    public static DashboardView dashboardView;
    public static Intake intakeSubsystem;
    public static Lifter lifterSubsystem;
    public static Shooter shooterSubsystem;
    public static IO controls;

    Command autonomousCommand;

    public void robotInit()
    {
        driveSubsystem = new SwerveDriveSteamworks();
        augerSubsystem = new Auger();
        climberSubsystem = new Climber();
        dashboardView = new DashboardView();
        intakeSubsystem = new Intake();
        lifterSubsystem = new Lifter(true);
        shooterSubsystem = new Shooter();
        leds = LED.getInstance();

        controls = IO.getInstance();  // IMPORTANT: Initialize IO after subsystems, so all subsystem parameters passed to commands are initialized

        CameraServer.getInstance().send(new CameraMode(VisionMode.robotDefault().getValue()));
    }

    public void operatorControl()
    {
        if (autonomousCommand != null) autonomousCommand.cancel();
        leds.set(LED.defaultLEDEffect);

        while (isOperatorControl() && isEnabled())
        {
            allPeriodic();
        }
    }

    public void autonomous()
    {
        leds.set(leds.getAllianceEffect());
        autonomousCommand =  Autonomous.setupSelectedMode();
        if (autonomousCommand != null) autonomousCommand.start();

        while (isAutonomous() && isEnabled())
        {
            allPeriodic();
        }
    }

    public void disabled()
    {
        leds.set(LED.defaultLEDEffect);

        while (isDisabled())
        {
            lifterSubsystem.reset();
            Gyro.getInstance().zeroYaw();
            log.info("TeamColor", (DriverStation.getInstance().getAlliance() == Alliance.Red) ? "Red" : "Blue");

            allPeriodic();
        }
    }

    public void test()
    {
        while (isTest() && isEnabled()) { }
    }

    private void allPeriodic()
    {
        log.info("Drive", driveSubsystem);
        log.info("High Gear", state.robotGear);
        log.info("Battery", DriverStation.getInstance().getBatteryVoltage());  // TODO .1 battery res
        Scheduler.getInstance().run();
    }
}
