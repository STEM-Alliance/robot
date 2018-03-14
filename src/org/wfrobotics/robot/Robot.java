package org.wfrobotics.robot;

import org.wfrobotics.reuse.background.BackgroundUpdater;
import org.wfrobotics.reuse.hardware.led.RevLEDs;
import org.wfrobotics.reuse.hardware.led.RevLEDs.PatternName;
import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.reuse.utilities.DashboardView;
import org.wfrobotics.reuse.utilities.MatchState2018;
import org.wfrobotics.robot.config.Autonomous;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.robotConfigs.HerdPractice;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;
import org.wfrobotics.robot.subsystems.LiftSubsystem;
import org.wfrobotics.robot.subsystems.WinchSubsystem;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// TODO 2019 switch to non-deprecated RobotBase
@SuppressWarnings("deprecation")
public class Robot extends SampleRobot
{
    private final BackgroundUpdater backgroundUpdater = new BackgroundUpdater(.005);
    private final Scheduler scheduler = Scheduler.getInstance();
    public static RobotConfig config;
    private final RobotState state = RobotState.getInstance();
    private final MatchState2018 matchState = MatchState2018.getInstance();

    public static TankSubsystem driveService;
    public static IntakeSubsystem intakeSubsystem;
    public static LiftSubsystem liftSubsystem;
    public static WinchSubsystem winch;
    public static Wrist wrist;
    public static DashboardView dashboardView = new DashboardView(416, 240, 20);//, new DashboardView(416, 240, 20)};

    public static IO controls;

    Command autonomousCommand;
    double lastPeriodicTime = 0;

    public static Spark led = new Spark(9);

    public void robotInit()
    {
        config = new HerdPractice();
        //config = new HerdVictor();

        driveService = TankSubsystem.getInstance();
        liftSubsystem = new LiftSubsystem(config);
        intakeSubsystem = new IntakeSubsystem(config);
        winch = new WinchSubsystem(config);
        wrist = new Wrist(config);

        controls = IO.getInstance();  // IMPORTANT: Initialize IO after subsystems, so all subsystem parameters passed to commands are initialized
        Autonomous.setupSelection();

        backgroundUpdater.register(driveService);
        backgroundUpdater.register(intakeSubsystem);
        backgroundUpdater.register(liftSubsystem);
    }

    public void operatorControl()
    {
        if (autonomousCommand != null) autonomousCommand.cancel();

        backgroundUpdater.start();
        led.set(RevLEDs.getValue(PatternName.Yellow));

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
        led.set(RevLEDs.getValue((m_ds.getAlliance() == Alliance.Red) ? PatternName.Red : PatternName.Blue));

        autonomousCommand =  Autonomous.getConfiguredCommand();
        if (autonomousCommand != null) autonomousCommand.start();

        while (isAutonomous() && isEnabled())
        {
            allPeriodic();
        }

        //        driveService.turnToHeading(-90);
        //        driveService.turnToHeading(0);
        //        driveService.turnToHeading(180);

    }

    public void disabled()
    {
        backgroundUpdater.stop();

        //        Autonomous.setupSelection();
        //        LiftGoHomes.reset();

        while (isDisabled())
        {
            matchState.update();

            // log.info("TeamColor", (m_ds.getAlliance() == Alliance.Red) ? "Red" : "Blue");
            driveService.zeroGyro();
            intakeSubsystem.onBackgroundUpdate();  // For cube distance sensor
            //            liftSubsystem.onBackgroundUpdate();  // Zero if possible

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
        wrist.reportState();
        driveService.reportState();
        state.reportState();

        // Scheduler
        double schedulerStart = Timer.getFPGATimestamp();
        scheduler.run();

        SmartDashboard.putNumber("Periodic Time ", Timer.getFPGATimestamp() - schedulerStart);
    }
}
