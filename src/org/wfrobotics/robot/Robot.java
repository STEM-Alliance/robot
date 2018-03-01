package org.wfrobotics.robot;

import org.wfrobotics.reuse.background.BackgroundUpdater;
import org.wfrobotics.reuse.hardware.led.RevLEDs;
import org.wfrobotics.reuse.utilities.DashboardView;
import org.wfrobotics.reuse.utilities.MatchState2018;
import org.wfrobotics.robot.config.Autonomous;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.robotConfigs.HerdVictor;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;
import org.wfrobotics.robot.subsystems.DriveService;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;
import org.wfrobotics.robot.subsystems.LED;
import org.wfrobotics.robot.subsystems.LiftSubsystem;
import org.wfrobotics.robot.subsystems.WinchSubsystem;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// TODO 2019 switch to non-deprecated RobotBase
@SuppressWarnings("deprecation")
public class Robot extends SampleRobot
{
    private final BackgroundUpdater backgroundUpdater = new BackgroundUpdater();
    private final Scheduler scheduler = Scheduler.getInstance();
    public static RobotConfig config;
    private final RobotState state = RobotState.getInstance();
    private final MatchState2018 matchState = MatchState2018.getInstance();

    public static DriveService driveService;
    public static IntakeSubsystem intakeSubsystem;
    public static LiftSubsystem liftSubsystem;
    public static WinchSubsystem winch;
    public static LED led;
    public static DashboardView[] dashboardView = {new DashboardView(416, 240, 15), new DashboardView(416, 240, 15)};

    public static IO controls;

    Command autonomousCommand;
    double lastPeriodicTime = 0;


    public void robotInit()
    {
        config = new HerdVictor();

        driveService = DriveService.getInstance();
        liftSubsystem = new LiftSubsystem(config);
        intakeSubsystem = new IntakeSubsystem(config);
        winch = new WinchSubsystem(config);
        led = new LED();
        led.setLed(RevLEDs.getValue(RevLEDs.PatternName.Fire_Large));
        controls = IO.getInstance();  // IMPORTANT: Initialize IO after subsystems, so all subsystem parameters passed to commands are initialized
        Autonomous.setupSelection();

        backgroundUpdater.register(intakeSubsystem);
        backgroundUpdater.register(liftSubsystem);

    }

    public void operatorControl()
    {
        if (autonomousCommand != null) autonomousCommand.cancel();

        backgroundUpdater.start();
        intakeSubsystem.setVertical(true);

        while (isOperatorControl() && isEnabled())
        {
            allPeriodic();
        }
    }

    public void autonomous()
    {
        if(!matchState.update())
        {
            // something went wrong, and we didn't get the match info data
            // TODO error?
        }

        backgroundUpdater.start();
        intakeSubsystem.setVertical(true);

        //        autonomousCommand =  new AutoSwitch1();
        autonomousCommand =  Autonomous.getConfiguredCommand();
        //        autonomousCommand = new SwitchChoice(new PrintTestCommand("R"), new PrintTestCommand("L"));

        if (autonomousCommand != null) autonomousCommand.start();

        while (isAutonomous() && isEnabled())
        {
            allPeriodic();
        }
    }

    public void disabled()
    {
        backgroundUpdater.stop();

        //        Autonomous.setupSelection();
        //        LiftGoHomes.reset();

        while (isDisabled())
        {
            // log.info("TeamColor", (m_ds.getAlliance() == Alliance.Red) ? "Red" : "Blue");
            driveService.zeroGyro();
            intakeSubsystem.onBackgroundUpdate();  // For cube distance sensor
            //            liftSubsystem.onBackgroundUpdate();  // Zero if possible
            led.setLed((m_ds.getAlliance() == Alliance.Red) ? RevLEDs.getValue(RevLEDs.PatternName.Strobe_Red) :  RevLEDs.getValue(RevLEDs.PatternName.Strobe_Blue));
            allPeriodic();
        }
    }

    public void test()
    {
        while (isTest() && isEnabled())
        {
            allPeriodic();
        }
    }

    private void allPeriodic()
    {
        // Update robot values to latest for this Scheduer iteration
        intakeSubsystem.reportState();
        liftSubsystem.reportState();
        state.reportState();

        // Scheduler
        double schedulerStart = Timer.getFPGATimestamp();
        scheduler.run();

        SmartDashboard.putNumber("Periodic Time ", Timer.getFPGATimestamp() - schedulerStart);
        SmartDashboard.putNumber("Battery V", RobotController.getInputVoltage());
        SmartDashboard.putNumber("Battery A", RobotController.getInputCurrent());
    }
}
