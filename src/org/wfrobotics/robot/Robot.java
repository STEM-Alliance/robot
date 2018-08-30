package org.wfrobotics.robot;

import org.wfrobotics.reuse.hardware.AutoTune;
import org.wfrobotics.reuse.hardware.LEDs;
import org.wfrobotics.reuse.hardware.lowleveldriver.RevLEDs.PatternName;
import org.wfrobotics.reuse.math.geometry.Pose2d;
import org.wfrobotics.reuse.subsystems.SubsystemRunner;
import org.wfrobotics.reuse.subsystems.background.BackgroundUpdater;
import org.wfrobotics.reuse.subsystems.background.RobotStateEstimator;
import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.reuse.utilities.ConsoleLogger;
import org.wfrobotics.reuse.utilities.DashboardView;
import org.wfrobotics.robot.auto.MatchState2018;
import org.wfrobotics.robot.config.Autonomous;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.SuperStructure;

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

    //public static WinchSubsystem winch;

    public static LEDs leds = new LEDs(9, PatternName.Yellow);
    public static IO controls;
    Command autonomousCommand;

    @Override
    public void robotInit()
    {
        //winch = WinchSubsystem.getInstance();  // TODO Can we remove?

        controls = IO.getInstance();  // Initialize IO after subsystems
        DashboardView.startPerformanceCamera();
        Autonomous.setupSelection();  // TODO Improve reliability

        //subsystems.register(IntakeSubsystem.getInstance());
        //subsystems.register(LiftSubsystem.getInstance());
        subsystems.register(SuperStructure.getInstance());
        //subsystems.register(Wrist.getInstance());
        subsystems.register(driveSubsystem);
        subsystems.registerReporter(state);
        subsystems.registerReporter(backgroundUpdater);
        subsystems.registerReporter(ConsoleLogger.getInstance());
        subsystems.registerReporter(AutoTune.getInstance());
        BackgroundUpdater.getInstance().register(RobotStateEstimator.getInstance());
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

        ConsoleLogger.getInstance().reportState();  // flush
        System.out.println("-------------\nRobot Tests\n-------------");
        leds.signalHumanPlayer();  // Pit safety
        Timer.delay(1.0);
        result &= driveSubsystem.runFunctionalTest(true);
        //result &= Wrist.getInstance().runFunctionalTest(true);
        //result &= IntakeSubsystem.getInstance().runFunctionalTest(true);
        //result &= LiftSubsystem.getInstance().runFunctionalTest(true);
        //result &= winch.runFunctionalTest(true);
        result &= SuperStructure.getInstance().runFunctionalTest(true);
        result &= leds.runFunctionalTest(result);
        ConsoleLogger.getInstance().reportState();
        System.out.println(String.format("Robot Tests: %s", (result) ? "SUCCESS" : "FAILURE"));
        Timer.delay(3.0);
    }

    @Override
    public void autonomousPeriodic()
    {
        subsystems.update();
    }

    @Override
    public void teleopPeriodic()
    {
        subsystems.update();
    }

    @Override
    public void disabledPeriodic()
    {
        matchState.update();

        driveSubsystem.zeroEncoders();
        driveSubsystem.setGyro(0.0);
        state.resetDriveState(Timer.getFPGATimestamp(), new Pose2d());
        //IntakeSubsystem.getInstance().onBackgroundUpdate();  // For cube distance sensor
        //LiftSubsystem.getInstance().zeroIfAtLimit();
        //Wrist.getInstance().zeroIfAtLimit();

        subsystems.update();
    }

    @Override
    public void testPeriodic()
    {
        //        leds.testScrollAll();
    }
}
