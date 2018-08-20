package org.wfrobotics.robot;

import org.wfrobotics.reuse.hardware.AutoTune;
import org.wfrobotics.reuse.hardware.LEDs;
import org.wfrobotics.reuse.hardware.RevLEDs.PatternName;
import org.wfrobotics.reuse.math.rigidtransform.RigidTransform2d;
import org.wfrobotics.reuse.subsystems.SubsystemRunner;
import org.wfrobotics.reuse.subsystems.background.BackgroundUpdater;
import org.wfrobotics.reuse.subsystems.background.RobotStateEstimator;
import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.reuse.utilities.ConsoleLogger;
import org.wfrobotics.reuse.utilities.DashboardView;
import org.wfrobotics.reuse.utilities.MatchState2018;
import org.wfrobotics.robot.config.Autonomous;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;
import org.wfrobotics.robot.subsystems.LiftSubsystem;
import org.wfrobotics.robot.subsystems.WinchSubsystem;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/** @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead */
public final class Robot extends IterativeRobot
{
    private final BackgroundUpdater backgroundUpdater = BackgroundUpdater.getInstance();
    private final RobotState state = RobotState.getInstance();
    private final MatchState2018 matchState = MatchState2018.getInstance();
    private static TankSubsystem driveSubsystem = TankSubsystem.getInstance();
    private final SubsystemRunner subsystems = SubsystemRunner.getInstance();

    public static IntakeSubsystem intakeSubsystem;
    public static LiftSubsystem liftSubsystem;
    public static WinchSubsystem winch;
    public static Wrist wrist;

    public static LEDs leds = new LEDs(9, PatternName.Yellow);
    public static IO controls;
    Command autonomousCommand;

    @Override
    public void robotInit()
    {
        intakeSubsystem = new IntakeSubsystem();
        liftSubsystem = new LiftSubsystem();
        winch = new WinchSubsystem();
        wrist = new Wrist();

        controls = IO.getInstance();  // Initialize IO after subsystems
        DashboardView.startQualityCamera();
        Autonomous.setupSelection();

        subsystems.register(intakeSubsystem);
        subsystems.register(liftSubsystem);
        subsystems.register(wrist);
        subsystems.register(driveSubsystem);
        backgroundUpdater.register(RobotStateEstimator.getInstance());
    }

    @Override
    public void autonomousInit()
    {
        leds.setAllianceColor(m_ds.getAlliance());
        leds.setRobotMode(true);
        backgroundUpdater.start(true);

        autonomousCommand =  Autonomous.getConfiguredCommand();
        if (autonomousCommand != null) autonomousCommand.start();
    }

    @Override
    public void teleopInit()
    {
        if (autonomousCommand != null) autonomousCommand.cancel();

        leds.setRobotMode(false);
        backgroundUpdater.start(false);

        driveSubsystem.setBrake(false);
    }

    @Override
    public void disabledInit()
    {
        driveSubsystem.log();
        backgroundUpdater.stop();

        driveSubsystem.setBrake(false);
    }

    @Override
    public void testInit()
    {
        boolean result = true;

        Timer.delay(0.5);
        result &= driveSubsystem.runFunctionalTest();
        result &= leds.testRobotSpecificColors();
        System.out.println(String.format("Robot Tests: %s", (result) ? "SUCCESS" : "FAILURE"));
    }

    @Override
    public void autonomousPeriodic()
    {
        allPeriodic();
    }

    @Override
    public void teleopPeriodic()
    {
        allPeriodic();
    }

    @Override
    public void disabledPeriodic()
    {
        matchState.update();

        driveSubsystem.zeroEncoders();
        driveSubsystem.setGyro(0.0);
        state.resetDriveState(Timer.getFPGATimestamp(), new RigidTransform2d());
        intakeSubsystem.onBackgroundUpdate();  // For cube distance sensor
        //            liftSubsystem.onBackgroundUpdate();  // Zero if possible

        allPeriodic();
    }

    @Override
    public void testPeriodic()
    {
        leds.testScrollAll();
    }

    private void allPeriodic()
    {
        subsystems.updateSensors();
        subsystems.runCommands();
        subsystems.reportState();

        state.reportState();
        backgroundUpdater.reportState();
        ConsoleLogger.reportState();
        AutoTune.getInstance().reportState();
    }
}
