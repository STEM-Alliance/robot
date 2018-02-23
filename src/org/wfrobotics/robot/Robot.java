package org.wfrobotics.robot;

import org.wfrobotics.reuse.background.BackgroundUpdater;
import org.wfrobotics.reuse.commands.drivebasic.DriveDistance;
import org.wfrobotics.reuse.subsystems.tank.TankService;
import org.wfrobotics.reuse.utilities.DashboardView;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.MatchState2018;
import org.wfrobotics.robot.config.Autonomous;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.robotConfigs.HerdVictor;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;
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
    //    public static HerdPractice config;
    public static HerdVictor config;

    private final BackgroundUpdater backgroundUpdater = new BackgroundUpdater();
    private final HerdLogger log = new HerdLogger(Robot.class);
    private final Scheduler scheduler = Scheduler.getInstance();
    private final RobotState state = RobotState.getInstance();
    private final MatchState2018 matchState = MatchState2018.getInstance();

    public static TankService driveService;
    public static IntakeSubsystem intakeSubsystem;
    public static LiftSubsystem liftSubsystem;
    public static WinchSubsystem winch;
    public static DashboardView dashboardView;

    public static IO controls;

    Command autonomousCommand;
    double lastPeriodicTime = 0;

    //    Spark led = new Spark(1);

    public void robotInit()
    {
        Autonomous.setupSendableChooser();
        config = new HerdVictor();
        //        config = new HerdVictor();

        driveService = TankService.getInstance();
        liftSubsystem = new LiftSubsystem(config);
        intakeSubsystem = new IntakeSubsystem(config);
        winch = new WinchSubsystem();

        // uncomment if using USB camera to stream video from roboRio
        dashboardView = new DashboardView(416, 240, 15);

        controls = IO.getInstance();  // IMPORTANT: Initialize IO after subsystems, so all subsystem parameters passed to commands are initialized

        // TODO default config?
        //CameraServer.getInstance();

        backgroundUpdater.register(intakeSubsystem);
        backgroundUpdater.register(liftSubsystem);

        //        led.set(.69);
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

        //        autonomousCommand =  Autonomous.setupAndReturnSelectedMode();
        autonomousCommand = new DriveDistance(12 * 22 + 0);
        if (autonomousCommand != null) autonomousCommand.start();

        while (isAutonomous() && isEnabled())
        {
            allPeriodic();
        }
    }

    public void disabled()
    {
        backgroundUpdater.stop();

        while (isDisabled())
        {
            driveService.zeroGyro();
            log.info("TeamColor", (m_ds.getAlliance() == Alliance.Red) ? "Red" : "Blue");
            intakeSubsystem.onBackgroundUpdate();  // For cube distance sensor

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
