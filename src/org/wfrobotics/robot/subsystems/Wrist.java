package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.LimitSwitch;
import org.wfrobotics.reuse.hardware.StallSense;
import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.robot.commands.wrist.WristZeroThenOpenLoop;
import org.wfrobotics.robot.config.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The wrist consists of a BAG motor to rotate the intake
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 */
public class Wrist extends PositionBasedSubsystem
{
    public static Wrist getInstance()
    {
        if (instance == null)
        {
            final RobotConfig config = RobotConfig.getInstance();
            final TalonSRX master = TalonFactory.makeClosedLoopTalon(config.WRIST_CLOSED_LOOP).get(0);
            final LimitSwitchNormal[] limitsConfig = new LimitSwitchNormal[] {
                LimitSwitchNormal.NormallyOpen, LimitSwitchNormal.NormallyOpen,
            };
            instance = new Wrist(master, limitsConfig);
        }
        return instance;
    }

    private static final double kFullRangeDegrees = 90.0;
    private static final int kTicksToTop = 5000;
    private final boolean kTuning;

    private static Wrist instance = null;
    private final StallSense stallSensor;

    private boolean stalled = false;

    private Wrist(TalonSRX masterTalon, LimitSwitchNormal[] limitsConfig)
    {
        super(masterTalon, limitsConfig, kTicksToTop / kFullRangeDegrees);
        RobotConfig config = RobotConfig.getInstance();
        kTuning = config.kWinchTuning;

        master.setSelectedSensorPosition(kTicksToTop, 0, 100);  // Start able to always reach limit switch
        master.configNeutralDeadband(config.kWristDeadband, 100);
        master.configOpenloopRamp(.1, 10);
        TalonFactory.configCurrentLimiting(master, 20, 40, 200);  // Adding with high numbers just in case
        master.setControlFramePeriod(ControlFrame.Control_3_General, 10);  // Slow down, wrist responsiveness not critical
        master.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 20, 100);  // Slow down, doesn't make decisions off this
        TalonFactory.configFastErrorReporting(master, kTuning);
        // TODO Try configAllowableClosedloopError()
        // TODO Try using Status_10_MotionMagic to improve motion?

        LimitSwitch.configSoftwareLimitF(master, kTicksToTop, true);
        LimitSwitch.configSoftwareLimitR(master, -500, true);

        stallSensor = new StallSense(master, 25.0, 0.1);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new WristZeroThenOpenLoop());
    }

    @Override
    public void cacheSensors(boolean isDisabled)
    {
        stalled = stallSensor.isStalled();
        super.cacheSensors(isDisabled);
    }

    @Override
    public void reportState()
    {
        super.reportState();
        SmartDashboard.putBoolean("Wrist Stalled", stalled);
    }

    /** Current sense limit switch is set. Only the top can trigger this. */
    public boolean isStalled()
    {
        return stalled;
    }

    @Override
    public void zeroIfAtLimit()
    {
        if (AtHardwareLimitBottom())
        {
            zeroEncoder();
        }
        else if (AtHardwareLimitTop() || isStalled())
        {
            master.setSelectedSensorPosition(kTicksToTop, 0, 0);
            hasZeroed = true;
        }
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));
        report.add(TalonChecker.checkFirmware(master));
        report.add(TalonChecker.checkEncoder(master));
        report.add(TalonChecker.checkFrameRates(master));

        int retries = 10;
        while (!hasZeroed() && retries-- > 0)
        {
            setOpenLoop(-1.0);
            Timer.delay(.2);
            zeroIfAtLimit();
        }
        setOpenLoop(0.0);
        report.add(AtHardwareLimitBottom(), "Bottom Limit Is Set");
        report.add(TalonChecker.checkSensorPhase(0.3, master));

        return report;
    }
}
