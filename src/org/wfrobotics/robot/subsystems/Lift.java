package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.LimitSwitch;
import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.robot.commands.lift.LiftZeroThenOpenLoop;
import org.wfrobotics.robot.config.LiftHeight;
import org.wfrobotics.robot.config.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * The elevator consists of two independently connected Mini CIM motors to raise/lower the intake and climber
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 */
public class Lift extends PositionBasedSubsystem
{
    public static Lift getInstance()
    {
        if (instance == null)
        {
            final RobotConfig config = RobotConfig.getInstance();
            final TalonSRX master = TalonFactory.makeClosedLoopTalon(config.LIFT_CLOSED_LOOP).get(0);
            final LimitSwitchNormal[] limitsConfig = config.LIFT_LIMIT_SWITCH_NORMALLY;
            instance = new Lift(master, limitsConfig);
        }
        return instance;
    }

    private static final int kTicksToTop = 27000;
    private static final double kTicksPerInch = kTicksToTop / 38.0;
    private static final double kFeedForwardHasCube = 0.25;  // TODO Keep track even if we tilt wrist
    private static final double kFeedForwardNoCube = 0.20;  // TODO Try increasing to make more buoyant
    private static final double kInchesGroundToZero = LiftHeight.Intake.get();
    private static final int kTickRateBrakeModeObserved = 500;
    private static final int kTickRateSlowEnough = kTickRateBrakeModeObserved + 200;
    private final int kSlotUp, kSlotDown;
    private final boolean kTuning;

    private static Lift instance = null;
    private final BaseMotorController follower;

    private Lift(TalonSRX masterTalon, LimitSwitchNormal[] limitsConfig)
    {
        super(masterTalon, limitsConfig, kTicksPerInch);
        RobotConfig config = RobotConfig.getInstance();
        kTuning = config.kLiftTuning;
        kSlotUp = config.LIFT_CLOSED_LOOP.gains.get(0).kSlot;
        kSlotDown = config.LIFT_CLOSED_LOOP.gains.get(1).kSlot;

        master.setSelectedSensorPosition(config.kLiftTicksStartup, 0, 100);
        master.configNeutralDeadband(0.1, 100);
        TalonFactory.configCurrentLimiting(master, 15, 30, 200);  // Observed 10A when holding
        TalonFactory.configFastErrorReporting(master, kTuning);
        //        master.configAllowableClosedloopError(0, 20, 10);
        //        master.configAllowableClosedloopError(1, 20, 10);
        master.configClosedloopRamp(0.15, 100);  // Soften reaching setpoint
        // TODO Try using Status_10_MotionMagic to improve motion?

        follower = TalonFactory.makeFollowers(master, config.LIFT_CLOSED_LOOP.masters.get(0)).get(0);

        LimitSwitch.configSoftwareLimitF(master, kTicksToTop, true);
        LimitSwitch.configSoftwareLimitR(master, -500, true);
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new LiftZeroThenOpenLoop());
    }

    /** Inches off ground */
    @Override
    public double getPosition()
    {
        return NativeToPosition(getPositionNative()) + kInchesGroundToZero;
    }

    /** Use to improve isFinished() criteria for closed loop commands? */
    public boolean onTarget()
    {
        return Math.abs(getVelocityNative()) < kTickRateSlowEnough;
    }

    @Override
    public void setOpenLoop(double percent)
    {
        final double speed = (AtHardwareLimitTop() && percent > 0.0) ? 0.0 : percent;

        setMotor(ControlMode.PercentOutput, speed);
    }

    @Override
    public void setClosedLoop(double inchesOffGround)
    {
        final double inchesFromZero = inchesOffGround - kInchesGroundToZero;
        final int slot = (inchesFromZero > getPositionNative()) ? kSlotUp : kSlotDown;

        master.selectProfileSlot(slot, 0);
        setMotor(ControlMode.MotionMagic, PositionToNative(inchesFromZero));  // Stalls motors
    }

    @Override
    protected void setMotor(ControlMode mode, double val)
    {
        final double antigravity= (state.robotHasCube) ? kFeedForwardHasCube : kFeedForwardNoCube;
        final double feedforward = (getPosition() > kInchesGroundToZero || mode == ControlMode.MotionMagic) ? antigravity : 0.0;

        master.set(mode, val, DemandType.ArbitraryFeedForward, feedforward);
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));
        report.add(TalonChecker.checkFirmware(master));
        report.add(TalonChecker.checkFirmware(follower));
        report.add(TalonChecker.checkEncoder(master));
        report.add(TalonChecker.checkFrameRates(master));
        report.add(TalonChecker.checkSensorPhase(0.3, master));

        return report;
    }
}
