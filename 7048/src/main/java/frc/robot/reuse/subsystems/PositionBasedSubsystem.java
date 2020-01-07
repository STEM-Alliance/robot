package frc.robot.reuse.subsystems;

import java.util.ArrayList;
import java.util.Optional;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.reuse.config.TalonConfig.ClosedLoopConfig;
import frc.robot.reuse.hardware.LimitSwitch;
import frc.robot.reuse.hardware.TalonFactory;
import frc.robot.reuse.hardware.LimitSwitch.Limit;

/**
 * STEM Alliance {@link EnhancedSubsystem} for subsystems which <b>control a mechinism
 * position</b>, where <i>safe</i> positions are <b>constrained to a limited range of motion</b>
 * @author STEM Alliance of Fargo Moorhead
 */
public abstract class PositionBasedSubsystem extends SubsystemBase
{
    /**
     * Config for {@link PositionBasedSubsystem}. Put in getInstance() or Robot Config.
     * @author STEM Alliance of Fargo Moorhead
     */
    public static class PositionConfig
    {
        /** Settings to configure the talon and any followers for use with the encoder */
        public ClosedLoopConfig kClosedLoop;
        /** Observed ticks from the encoder (zero) position to the top (full range) position */
        public int kTicksToTop;
        /** Observed real units (inches or degrees) from the encoder (zero) position to the top (full range) position. */
        public double kFullRangeInchesOrDegrees;
        /** Inverts your sensor if it reads "on" when it shouldn't */
        public Optional<Boolean> kHardwareLimitNormallyOpenB = Optional.empty();
        /** Inverts your sensor if it reads "on" when it shouldn't */
        public Optional<Boolean> kHardwareLimitNormallyOpenT = Optional.empty();
        /** A typical value is slightly below zero (ex: -500) */
        public Optional<Integer> kSoftwareLimitB = Optional.empty();
        /** A typical value is kTicksToTop */
        public Optional<Integer> kSoftwareLimitT = Optional.empty();
        /** Always apply this percent voltage to keep the subsystem from dropping over time due to its own weight */
        public Optional<Double> kFeedForward = Optional.empty();
        /** Tuning the PID(s). Enables faster, additional SmartDash prints */
        public Optional<Boolean> kTuning = Optional.empty();
    }

    /** Feedforward is not final in case subsystem wants to change based on having a gamepiece or not */
    protected double kFeedForward;
    protected final double kFullRangeInchesOrDegrees;
    protected final int kTicksToTop;
    protected final double kTicksPerInchOrDegree;
    protected final boolean kTuning;

    protected final TalonSRX master;
    protected final ArrayList<BaseMotorController> followers;
    /** Hardware limit switches. The Talon automatically prevents the motor from going to a position beyond these sensors. */
    protected final LimitSwitch limits;
    /** Saved values of our sensors so we don't have to do repeated reads from the hardware layer */
    protected PositionCachedIO cachedIO = new PositionCachedIO();

    protected boolean hasZeroed;

    public PositionBasedSubsystem(PositionConfig config)
    {
        kFeedForward = config.kFeedForward.orElse(0.0);
        kFullRangeInchesOrDegrees = config.kFullRangeInchesOrDegrees;
        kTicksToTop = config.kTicksToTop;
        kTicksPerInchOrDegree = kTicksToTop / config.kFullRangeInchesOrDegrees;
        kTuning = config.kTuning.orElse(false);

        master = TalonFactory.makeClosedLoopTalon(config.kClosedLoop).get(0);
        master.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_10Ms, 20);
        master.configVelocityMeasurementWindow(1, 20);
        master.configNeutralDeadband(.05, 20);  // Assume open loop on joystick needs small deadband, set in child constructor if a different value is needed
        TalonFactory.configFastErrorReporting(master, kTuning);

        followers = TalonFactory.makeFollowers(master, config.kClosedLoop.masters.get(0));

        limits =  new LimitSwitch(master, config.kHardwareLimitNormallyOpenB.orElse(true), config.kHardwareLimitNormallyOpenT.orElse(true));
        LimitSwitch.configSoftwareLimitR(master, config.kSoftwareLimitB.orElse(0), config.kSoftwareLimitB.isPresent());
        LimitSwitch.configSoftwareLimitF(master, config.kSoftwareLimitT.orElse(0), config.kSoftwareLimitT.isPresent());

        hasZeroed = false;
    }

    /** Save {@link PositionCachedIO} values, handle Subsystem zeroing */
    public void cacheSensors(boolean isDisabled)
    {
        if (!isDisabled)
        {
            cachedIO.positionTicks = master.getSelectedSensorPosition(0);
            cachedIO.velocityTicksPer100ms = master.getSelectedSensorVelocity(0);
            cachedIO.limitSwitchB = limits.isSet(Limit.REVERSE);

            zeroIfAtLimit();
        }
    }

    public void reportState()
    {
        SmartDashboard.putString(String.format("%s Command", getName()), getCurrentCommand().getName());
        SmartDashboard.putNumber(String.format("%s Position", getName()), getPosition());
        SmartDashboard.putBoolean(String.format("%s B", getName()), AtHardwareLimitBottom());
        SmartDashboard.putBoolean(String.format("%s Zeroed",getName()), hasZeroed());
        if (kTuning)
        {
            SmartDashboard.putNumber(String.format("%s Position Native", getName()), getPositionNative());
            SmartDashboard.putNumber(String.format("%s Velocity Native", getName()), getVelocityNative());
            SmartDashboard.putNumber(String.format("%s Error",getName()), master.getClosedLoopError());
        }
    }

    /**
     * Control the {@link PositionBasedSubsystem} with a <b>setpoint, with PID feedback</b>.
     * Override if your Subsystem needs special logic to determine setpoint or gain scheduling.
     * */
    public void setClosedLoop(double positionSetpoint)
    {
        setMotor(ControlMode.MotionMagic, PositionToNative(positionSetpoint));
    }

    /**
     * Control the {@link PositionBasedSubsystem} with the <b>Joystick, without PID feedback</b>.
     * Override if your Subsystem needs special logic to determine percent.
     */
    public void setOpenLoop(double percent)
    {
        setMotor(ControlMode.PercentOutput, percent);
    }

    /**
     *  Check and handle if Subsystem is at a limit.
     *  Override if your Subsystem also zeros when stalled, at top hardware limit, etc.
     */
    public void zeroIfAtLimit()
    {
        if(AtHardwareLimitBottom())
        {
            zeroEncoder();
        }
    }

    public boolean AtHardwareLimitBottom()
    {
        return cachedIO.limitSwitchB;
    }

    /**
     * In inches or degrees from encoder.
     * Override if your coordinate system has zero other than encoder (ex: inches from ground).
     */
    public double getPosition()
    {
        return NativeToPosition(getPositionNative());
    }

    /** Encoder has zeroed at any point, aka Subsystem is ready to go! */
    public boolean hasZeroed()
    {
        return hasZeroed;
    }

    /** <b>Set the motor(s)</b>. Apply any <b>feedforward used in both</b> open & closed loop here. */
    protected void setMotor(ControlMode mode, double setpoint)
    {
        master.set(mode, setpoint, DemandType.ArbitraryFeedForward, kFeedForward);
    }

    /** <b>Reset encoder position to be zero</b> and record zeroing has happened: Position is now known */
    protected void zeroEncoder()
    {
        master.setSelectedSensorPosition(0);
        hasZeroed = true;
    }

    /** In encoder units */
    protected double getPositionNative()
    {
        return cachedIO.positionTicks;
    }

    /** In encoder units */
    public double getVelocityNative()
    {
        return cachedIO.velocityTicksPer100ms;
    }

    /** Unit conversion */
    protected double PositionToNative(double inchesOrDegrees)
    {
        return inchesOrDegrees * kTicksPerInchOrDegree;
    }

    /** Unit conversion */
    protected double NativeToPosition(double ticks)
    {
        return ticks / kTicksPerInchOrDegree;
    }

    protected static class PositionCachedIO
    {
        public int positionTicks = 0;
        public int velocityTicksPer100ms = 0;
        public boolean limitSwitchB = false;
    }
}
