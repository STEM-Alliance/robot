package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.robot.commands.lift.LiftZeroThenOpenLoop;
import org.wfrobotics.robot.config.LiftHeight;
import org.wfrobotics.robot.config.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

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
            instance = new Lift(RobotConfig.getInstance().getLiftConfig());
        }
        return instance;
    }

    private static final double kFeedForwardHasCube = 0.25;  // TODO Keep track even if we tilt wrist
    private static final double kFeedForwardNoCube = 0.20;  // TODO Try increasing to make more buoyant
    private static final double kInchesGroundToZero = LiftHeight.Intake.get();
    private static final int kTickRateBrakeModeObserved = 500;
    private static final int kTickRateSlowEnough = kTickRateBrakeModeObserved + 200;
    private final int kSlotUp, kSlotDown;

    private static Lift instance = null;
    private final BaseMotorController follower;

    private Lift(PositionConfig positionConfig)
    {
        super(positionConfig);

        kSlotUp = positionConfig.kClosedLoop.gains.get(0).kSlot;
        kSlotDown = positionConfig.kClosedLoop.gains.get(1).kSlot;

        master.setSelectedSensorPosition(RobotConfig.kLiftTicksStartup, 0, 100);
        TalonFactory.configCurrentLimiting(master, 15, 30, 200);  // Observed 10A when holding
        //        master.configAllowableClosedloopError(0, 20, 10);
        //        master.configAllowableClosedloopError(1, 20, 10);
        master.configClosedloopRamp(0.15, 100);  // Soften reaching setpoint
        // TODO Try using Status_10_MotionMagic to improve motion?

        follower = TalonFactory.makeFollowers(master, positionConfig.kClosedLoop.masters.get(0)).get(0);
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
