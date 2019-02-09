package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.robot.commands.lift.LiftOpenLoop;
import org.wfrobotics.robot.config.LiftHeight;
import org.wfrobotics.robot.config.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

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

    private static final double kFeedForwardHasCargo = 0.0;  // TODO Tune
    private static final double kFeedForwardNoCargo = 0.0;  // TODO Tune
    private static final double kInchesGroundToZero = LiftHeight.HatchLow.get();
    private static final int kTickRateBrakeModeObserved = 0;  // TODO Tune
    private static final int kTickRateSlowEnough = kTickRateBrakeModeObserved + 0;  // TODO Tune

    DoubleSolenoid popper0 = new DoubleSolenoid(0, 0, 1);

    private static Lift instance = null;
    private final BaseMotorController follower;

    private Lift(PositionConfig positionConfig)
    {
        super(positionConfig);

        master.setSelectedSensorPosition(RobotConfig.kLiftTicksStartup, 0, 100);
        TalonFactory.configCurrentLimiting(master, 15, 30, 200);  // TODO Tune
        master.configClosedloopRamp(0.15, 100);  // Soften reaching setpoint TODO Tune

        follower = TalonFactory.makeFollowers(master, positionConfig.kClosedLoop.masters.get(0)).get(0);  // TODO
    }
    public void setPoppers(boolean out)
    {
        Value desired = (out) ? Value.kForward : Value.kReverse;
        popper0.set(desired);
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new LiftOpenLoop());
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

        setMotor(ControlMode.MotionMagic, PositionToNative(inchesFromZero));  // Stalls motors
    }

    @Override
    protected void setMotor(ControlMode mode, double val)
    {
        final boolean hasGamePiece = true;  // TODO get from intake
        final double antigravity= (hasGamePiece) ? kFeedForwardHasCargo : kFeedForwardNoCargo;
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
