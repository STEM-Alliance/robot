package org.wfrobotics.robot;

import java.util.ArrayList;
import java.util.Arrays;

import org.wfrobotics.reuse.hardware.AutoTune;
import org.wfrobotics.reuse.hardware.led.RevLEDs;
import org.wfrobotics.reuse.hardware.led.RevLEDs.PatternName;
import org.wfrobotics.reuse.subsystems.background.BackgroundUpdater;
import org.wfrobotics.reuse.subsystems.background.RobotStateEstimator;
import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.messages.VisionMessageConfig;
import org.wfrobotics.reuse.subsystems.vision.messages.VisionMessageTargets;
import org.wfrobotics.reuse.utilities.ConsoleLogger;
import org.wfrobotics.reuse.utilities.DashboardView;
import org.wfrobotics.reuse.utilities.MatchState2018;
import org.wfrobotics.robot.config.Autonomous;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;
import org.wfrobotics.robot.subsystems.LiftSubsystem;
import org.wfrobotics.robot.subsystems.WinchSubsystem;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class Robot extends IterativeRobot
{
    private final BackgroundUpdater backgroundUpdater = new BackgroundUpdater(.005);
    private final Scheduler scheduler = Scheduler.getInstance();
    private final RobotState state = RobotState.getInstance();
    private final MatchState2018 matchState = MatchState2018.getInstance();
    private static TankSubsystem driveSubsystem = TankSubsystem.getInstance();

    public static IntakeSubsystem intakeSubsystem;
    public static LiftSubsystem liftSubsystem;
    public static WinchSubsystem winch;
    public static Wrist wrist;

    public static DashboardView dashboardView = new DashboardView(416, 240, 20);
    public static IO controls;
    Command autonomousCommand;
    double lastPeriodicTime = 0.0;

    public static Spark led = new Spark(9);

    public static CameraServer visionServer;

    @SuppressWarnings("deprecation")
    @Override
    public void robotInit()
    {
        liftSubsystem = new LiftSubsystem();
        intakeSubsystem = new IntakeSubsystem();
        winch = new WinchSubsystem();
        wrist = new Wrist();

        visionServer = CameraServer.getInstance();
        visionServer.SetConfig(new VisionMessageConfig(0,1,
                                        new ArrayList<>(Arrays.asList(new Boolean[] {true, false}))));
        visionServer.AddListener(new VisionListener());

        //        VisionProcessor processor = new VisionProcessor();
        //        visionServer.AddListener(processor);

        controls = IO.getInstance();  // IMPORTANT: Initialize IO after subsystems, so all subsystem parameters passed to commands are initialized
        Autonomous.setupSelection();

        backgroundUpdater.register(driveSubsystem);
        backgroundUpdater.register(intakeSubsystem);
        backgroundUpdater.register(RobotStateEstimator.getInstance());
    }

    @Override
    public void autonomousInit()
    {
        RobotStateEstimator.getInstance().reset();
        backgroundUpdater.start();

        driveSubsystem.setBrake(true);
        led.set(RevLEDs.getValue((m_ds.getAlliance() == Alliance.Red) ? PatternName.Red : PatternName.Blue));

        autonomousCommand =  Autonomous.getConfiguredCommand();
        if (autonomousCommand != null) autonomousCommand.start();
    }

    @Override
    public void autonomousPeriodic()
    {
        allPeriodic();
    }

    @Override
    public void teleopInit()
    {
        if (autonomousCommand != null) autonomousCommand.cancel();

        RobotStateEstimator.getInstance().reset();
        backgroundUpdater.start();

        driveSubsystem.setBrake(false);
        led.set(RevLEDs.getValue(PatternName.Yellow));
    }

    @Override
    public void teleopPeriodic()
    {
        allPeriodic();
    }

    @Override
    public void disabledInit()
    {
        backgroundUpdater.stop();

        driveSubsystem.setBrake(false);
        led.set(RevLEDs.getValue(PatternName.Yellow));
    }

    @Override
    public void disabledPeriodic()
    {
        matchState.update();

        driveSubsystem.zeroEncoders();
        driveSubsystem.zeroGyro();
        state.resetDriveState();
        intakeSubsystem.onBackgroundUpdate();  // For cube distance sensor
        //            liftSubsystem.onBackgroundUpdate();  // Zero if possible

        allPeriodic();
    }

    @Override
    public void testInit()
    {
        Timer.delay(0.5);
    }

    @Override
    public void testPeriodic()
    {
        allPeriodic();
    }

    private void allPeriodic()
    {
        intakeSubsystem.updateSensors();
        liftSubsystem.updateSensors();
        wrist.updateSensors();
        driveSubsystem.updateSensors();

        double schedulerStart = Timer.getFPGATimestamp();
        scheduler.run();

        SmartDashboard.putNumber("Periodic Time ", Timer.getFPGATimestamp() - schedulerStart);
        intakeSubsystem.reportState();
        liftSubsystem.reportState();
        wrist.reportState();
        driveSubsystem.reportState();
        state.reportState();
        backgroundUpdater.reportState();
        ConsoleLogger.reportState();
        AutoTune.getInstance().reportState();
    }
    public class VisionListener implements CameraServer.CameraListener
    {
        private final RobotState state = RobotState.getInstance();

        public void Notify(VisionMessageTargets message)
        {
            state.addVisionUpdate(message);
        }
    }
    public static class VisionProcessor implements CameraServer.CameraListener
    {
        private VisionMessageTargets rx = null;

        public synchronized void Notify(VisionMessageTargets message)
        {
            rx = message;
        }

        public void onBackgroundUpdate()
        {
            VisionMessageTargets update;
            synchronized (this)
            {
                if (rx != null)
                {
                    return;
                }
                update = rx;
                rx = null;
            }
            System.out.println(update.fps);
        }
    }
}
