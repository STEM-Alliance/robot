package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.reuse.utilities.ConsoleLogger;
import org.wfrobotics.robot.commands.elevator.ElevatorOpenLoop;
import org.wfrobotics.robot.config.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

/**
 * The elevator consists of two independently connected Mini CIM motors to raise/lower the intake and climber
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 */
public class Elevator extends PositionBasedSubsystem
{
    public static Elevator getInstance()
    {
        if (instance == null)
        {
            instance = new Elevator(RobotConfig.getInstance().getElevatorConfig());
        }
        return instance;
    }

    private static final double kFeedForwardHasCargo = 0.0;  // TODO Tune
    private static final double kFeedForwardNoCargo = 0.0;  // TODO Tune
    private static final double kInchesGroundToZero = 26.5;  // Practice bot
    private static final int kTickRateBrakeModeObserved = 0;  // TODO Tune
    private static final int kTickRateSlowEnough = kTickRateBrakeModeObserved + 200;  // TODO Tune

    private static Elevator instance = null;
    private final DoubleSolenoid shifter;

    public enum LIFT_STATE {start, lowH, midH, highH, lowC, midC, highC}
    LIFT_STATE liftState = LIFT_STATE.start;

    public void setLiftState(LIFT_STATE state)
    {
        liftState = state;
    }

    public LIFT_STATE getLiftState()
    {
        return liftState;
    }

    private Elevator(PositionConfig positionConfig)
    {
        super(positionConfig);
        final RobotConfig config = RobotConfig.getInstance();

        master.setSelectedSensorPosition(RobotConfig.kElevatorTicksStartup, 0, 100);
        //        TalonFactory.configCurrentLimiting(master, 15, 30, 200);  // TODO Tune
        //        master.configClosedloopRamp(0.15, 100);  // Soften reaching setpoint TODO Tune

        if (allFollowersAreTalons())
        {
            TalonFactory.configCurrentLimiting(master, 30, 35, 20);
            for (BaseMotorController follower : followers)
            {
                TalonFactory.configCurrentLimiting(((TalonSRX) follower), 30, 35, 20);
            }
        }

        shifter = new DoubleSolenoid(0, config.kAddressSolenoidShifterF, config.kAddressSolenoidShifterB);

        setShifter(false);
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new ElevatorOpenLoop());
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

    public void setShifter(boolean liftNotClimb)
    {
        Value desired = (liftNotClimb) ? Value.kForward : Value.kReverse;
        shifter.set(desired);
    }

    private boolean allFollowersAreTalons()
    {
        ClosedLoopConfig config = RobotConfig.getInstance().getElevatorConfig().kClosedLoop;
        boolean result = config.masters.get(0).areFollowersAllTalons();

        if (!result)
        {
            ConsoleLogger.warning("Followers are not all talons, cannot config current limiting");
        }

        return result;
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();
        ClosedLoopConfig config = RobotConfig.getInstance().getElevatorConfig().kClosedLoop;

        report.add(getDefaultCommand().doesRequire(this));
        report.add(TalonChecker.checkClosedLoopConfig(config));
        report.add(allFollowersAreTalons());
        report.add(TalonChecker.checkFirmware(master));
        report.add(TalonChecker.checkFirmware(followers));
        report.add(TalonChecker.checkEncoder(master));
        report.add(TalonChecker.checkFrameRates(master));
        report.add(TalonChecker.checkSensorPhase(0.3, master));

        return report;
    }
}
