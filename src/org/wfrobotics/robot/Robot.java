package org.wfrobotics.robot;

import org.wfrobotics.reuse.config.AutoRunner;
import org.wfrobotics.reuse.hardware.AutoTune;
import org.wfrobotics.reuse.hardware.LEDs;
import org.wfrobotics.reuse.hardware.lowleveldriver.RevLEDs.PatternName;
import org.wfrobotics.reuse.math.geometry.Pose2d;
import org.wfrobotics.reuse.subsystems.SubsystemRunner;
import org.wfrobotics.reuse.subsystems.background.BackgroundUpdater;
import org.wfrobotics.reuse.subsystems.background.RobotStateEstimator;
import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.VisionListener;
import org.wfrobotics.reuse.subsystems.vision.messages.VisionMessageConfig;
import org.wfrobotics.reuse.utilities.ConsoleLogger;
import org.wfrobotics.reuse.utilities.DashboardView;
import org.wfrobotics.reuse.utilities.Testable;
import org.wfrobotics.robot.config.Auto;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.MatchState2018;
import org.wfrobotics.robot.paths.TrajectoryGenerator;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.Lift;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.robot.subsystems.Winch;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;

/**
 * Robot: Victor - 2018
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 * */
public final class Robot extends IterativeRobot
{
    private final BackgroundUpdater backgroundUpdater = BackgroundUpdater.getInstance();
    private final RobotState state = RobotState.getInstance();
    private final MatchState2018 matchState = MatchState2018.getInstance();
    private static TankSubsystem driveSubsystem = TankSubsystem.getInstance();
    private final SubsystemRunner subsystems = SubsystemRunner.getInstance();
    private final AutoRunner autos = AutoRunner.getInstance();

    public static LEDs leds = new LEDs(9, PatternName.Yellow);
    public static IO controls;
    public final CameraServer visionServer = CameraServer.getInstance();

    @Override
    public void robotInit()
    {
        visionServer.SetConfig(new VisionMessageConfig(0));
        //        visionServer.SetConfig(new VisionMessageConfig(0,1, new ArrayList<>(Arrays.asList(new Boolean[] {true, true}))));
        TrajectoryGenerator.getInstance().generateTrajectories();

        VisionListener listener = new VisionListener();
        visionServer.AddListener(listener);

        //        VisionProcessor processor = new VisionProcessor();
        //        visionServer.AddListener(processor);

        controls = IO.getInstance();  // Initialize IO after subsystems
        DashboardView.startPerformanceCamera();

        subsystems.register(Intake.getInstance());
        subsystems.register(Lift.getInstance());
        subsystems.register(SuperStructure.getInstance());
        subsystems.register(Winch.getInstance());
        subsystems.register(Wrist.getInstance());
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
        leds.setForAuto(m_ds.getAlliance());
        backgroundUpdater.start(true);

        autos.startMode();
    }

    @Override
    public void teleopInit()
    {
        autos.stopMode();

        leds.setForTeleop();
        backgroundUpdater.start(false);
    }

    @Override
    public void disabledInit()
    {
        autos.stopMode();
        driveSubsystem.log();  // TODO Move to updateSensors(isDisabled)?
        backgroundUpdater.stop();

        if (Math.abs(Robot.controls.getThrottle()) < 0.15)  // Add stop to subsystem or background update?
        {
            driveSubsystem.setBrake(false);
        }
    }

    @Override
    public void testInit()
    {
        autos.stopMode();
        boolean result = true;

        // TODO Pass SubsystemRunner a Supplier of robot-specific tests instead?

        ConsoleLogger.getInstance().reportState();  // flush
        System.out.println("-------------\nRobot Tests\n-------------");
        leds.signalHumanPlayer();  // Pit safety
        Timer.delay(1.0);
        result &= Testable.run(driveSubsystem, true);
        result &= Testable.run(Intake.getInstance(), true);
        result &= Testable.run(Lift.getInstance(), true);
        result &= Testable.run(SuperStructure.getInstance(), true);
        result &= Testable.run(Winch.getInstance(), true);
        result &= Testable.run(Wrist.getInstance(), true);
        result &= Testable.run(Auto.factory, false);
        result &= Testable.run(autos, false);
        result &= Testable.run(leds, result);
        ConsoleLogger.getInstance().reportState();
        System.out.println(String.format("Robot Tests: %s", (result) ? "SUCCESS" : "FAILURE"));
        Timer.delay(3.0);  // Display result
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
        matchState.update();  // TODO Move to AutoSelector?

        // TODO Remove some of these if UpdateSensors(isDisabled) worked
        driveSubsystem.zeroEncoders();
        driveSubsystem.setGyro(0.0);
        state.resetDriveState(Timer.getFPGATimestamp(), new Pose2d());
        Intake.getInstance().onBackgroundUpdate();  // For cube distance sensor
        Lift.getInstance().zeroIfAtLimit();
        Wrist.getInstance().zeroIfAtLimit();

        subsystems.update();
    }

    @Override
    public void testPeriodic()
    {
        leds.testScrollAll();
    }
}
