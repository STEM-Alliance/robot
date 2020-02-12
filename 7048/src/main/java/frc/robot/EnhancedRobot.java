package frc.robot;

import org.wfrobotics.reuse.config.AutoFactory;
import org.wfrobotics.reuse.config.AutoRunner;
import frc.robot.config.EnhancedIO;
import frc.robot.config.EnhancedRobotConfig;
import org.wfrobotics.reuse.hardware.LEDs;
import org.wfrobotics.reuse.hardware.NoLEDs;
import org.wfrobotics.reuse.subsystems.SubsystemRunner;
import org.wfrobotics.reuse.subsystems.background.BackgroundUpdater;
import org.wfrobotics.reuse.subsystems.background.RobotStateEstimator;
import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.reuse.utilities.ConsoleLogger;
import org.wfrobotics.reuse.utilities.DashboardView;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;

/**
 * Base robot for STEM Alliance FRC teams
 * @author STEM Alliance of Fargo Moorhead
 * */
public abstract class EnhancedRobot extends TimedRobot
{
    protected final BackgroundUpdater backgroundUpdater = BackgroundUpdater.getInstance();
    protected final SubsystemRunner subsystems = SubsystemRunner.getInstance();
    protected final AutoRunner autos = AutoRunner.getInstance();

    public static LEDs leds = new NoLEDs();

    /** Register subsystems specific to this robot with {@link SubsystemRunner}. Reuse subsystems (ex: {@link TankSubsystem}) are automatically registered. */
    protected abstract void registerRobotSpecific();
   
    protected static RobotStateBase state;
    protected static EnhancedRobotConfig config;
    protected static EnhancedIO buttons;

    protected EnhancedRobot(EnhancedRobotConfig config_, RobotStateBase state_, EnhancedIO buttons_)
    {
        super();
        config = config_;
        state = state_;
        buttons = buttons_;
    }

    public static RobotStateBase getState() { return state; }
    public static EnhancedRobotConfig getConfig() { return config; }
    public static EnhancedIO getIO() { return buttons; }

    @Override
    public void robotInit()
    {
        registerRobotSpecific();
        registerTank();
        subsystems.registerReporter(state);
        subsystems.registerReporter(backgroundUpdater);
        subsystems.registerReporter(ConsoleLogger.getInstance());
        // subsystems.registerReporter(AutoTune.getInstance());
        subsystems.registerTest(AutoFactory.getInstance());

        buttons.assignButtons();                // Initialize Buttons after subsystems
        AutoFactory.getInstance().onSelectionChanged();  // Set default auto mode
        if (getConfig().cameraStream.orElse(false))
        {
            DashboardView.startPerformanceCamera();
        }
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
        backgroundUpdater.stop();
    }

    @Override
    public void testInit()
    {
        autos.stopMode();

        leds.signalHumanPlayer();  // Pit safety
        boolean result = subsystems.runFunctionalTests();
        leds.signalFunctionalTestResult(result);
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
        subsystems.update();
    }

    @Override
    public void testPeriodic()
    {

    }

    private void registerTank()
    {
        subsystems.register(TankSubsystem.getInstance());
        if (config.getTankConfig().CLOSED_LOOP_ENABLED)
        {
            BackgroundUpdater.getInstance().register(RobotStateEstimator.getInstance());
        }
    }
}
