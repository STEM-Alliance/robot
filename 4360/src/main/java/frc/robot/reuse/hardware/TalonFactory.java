package frc.robot.reuse.hardware;

import java.util.ArrayList;
import java.util.List;


import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import frc.robot.reuse.config.TalonConfig.ClosedLoopConfig;
import frc.robot.reuse.config.TalonConfig.FollowerConfig;
import frc.robot.reuse.config.TalonConfig.Gains;
import frc.robot.reuse.config.TalonConfig.MasterConfig;
import frc.robot.reuse.hardware.lowleveldriver.LazyTalon;
import frc.robot.reuse.utilities.ConsoleLogger;

/**
 * Configures all of the TalonSRX's
 * @author STEM Alliance of Fargo Moorhead
 */
public final class TalonFactory
{
    public static final int kTimeoutMs = 20;  // Use zero during teleop, something longer in Subsystem constructors

    private TalonFactory() { /** No instances */ };

    /** Simple, <b>single threshold</b> current limiting. Start with this. Followers have same percent output as master - safe assumption to only limit master. */
    public static void configCurrentLimiting(TalonSRX t, int continuousAmps)
    {
        configCurrentLimiting(t, continuousAmps, 0, 0);
    }

    /** <b>Two threshold</b> current limiting. May have better performance during setpoint changes. Allows a brief, <i>desirable</i> spike in current. Followers have same percent output as master - safe assumption to only limit master. */
    public static void configCurrentLimiting(TalonSRX t, int continuousAmps, int peakAmps, int peakMs)
    {
        t.configPeakCurrentLimit(peakAmps, kTimeoutMs);
        t.configPeakCurrentDuration(peakMs, kTimeoutMs);
        t.configContinuousCurrentLimit(continuousAmps, kTimeoutMs);
        t.enableCurrentLimit(true);
    }

    /** Increases <i>rate</i> that RIO gets new <i>closed loop error</i>values. Helpful in PID tuning. Usually remove this if not tuning. */
    public static void configFastErrorReporting(TalonSRX t, boolean enable)
    {
        t.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, (enable) ? 10 : 100, kTimeoutMs);  // This is tunable, so slow value is still faster to help see problems easier and not throw stale frame warning
    }

    /** Reduce this Talon's CAN messaging rate so <i>other<i/> (closed loop) Talons have better performance */
    public static void configOpenLoopOnly(TalonSRX t)
    {
        t.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1000, kTimeoutMs);     // Sensor position, sensor velocity, current, sticky faults, selected profile
        t.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 1000, kTimeoutMs);    // Relative-specific
        t.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat, 1000, kTimeoutMs);   // Analog sensor position + velocity, temperature, battery voltage, selected feedback sensor for RIO to query
        t.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, 1000, kTimeoutMs);    // Absolute-specific
        t.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 1000, kTimeoutMs);  // Active trajectory point target position, velocity, heading for RIO to query
        t.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 255, kTimeoutMs);   // Closed-loop error, integral accumulator, derivative term, doesn't seem to allow values les than 255
        t.setStatusFramePeriod(StatusFrameEnhanced.Status_14_Turn_PIDF1, 1000, kTimeoutMs);
        //        t.setStatusFramePeriod(StatusFrameEnhanced.Status_15_FirmareApiStatus, 1000, kTimeoutMs);  // TODO Okay to do? What is default? Test this
    }

    /** May make the <b>talon output more repeatable</b>, without tuning out any added noise from adding this feature. */
    public static void configVoltageCompensation(BaseMotorController t)
    {
        configVoltageCompensation(t, 12.0, 32);
    }

    /** Makes the talon output <i>independent</i> of battery voltage, potentially making the <b>talon output more repeatable</b> but may also add noise */
    public static void configVoltageCompensation(BaseMotorController t, double voltagePeak, int filterMs)
    {
        t.configVoltageCompSaturation(voltagePeak, kTimeoutMs);  // Default is 0
        t.configVoltageMeasurementFilter(filterMs, kTimeoutMs);  // Default is 32
        t.enableVoltageCompensation(true);
    }

    /** Create Talon capable of being <b>PID controlled</b> based on an encoder */
    public static List<TalonSRX> makeClosedLoopTalon(ClosedLoopConfig config)
    {
        List<TalonSRX> talons = new ArrayList<TalonSRX>();

        if (config.masters.size() == 0)
        {
            ConsoleLogger.defcon1(String.format("Number masters in %s config: %d", config.name, config.masters.size()));
        }
        if (config.gains.size() == 0)
        {
            ConsoleLogger.defcon1(String.format("Number gains in %s config: %d", config.name, config.gains.size()));
        }

        for (MasterConfig m : config.masters)
        {
            TalonSRX t = makeTalon(m.kAddress);
            boolean hasSetMotionMagic = false;

            configEncoder(t);
            t.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 20, kTimeoutMs);  // Greater than robot update rate

            t.setNeutralMode(NeutralMode.Brake);
            t.setInverted(m.kInvert);
            t.setSensorPhase(m.kSensorPhase.orElse(false));

            for (Gains g : config.gains)
            {
                t.selectProfileSlot(g.kSlot, 0);
                if (!hasSetMotionMagic && g.kCruiseVelocity.isPresent() && g.kAcceleration.isPresent())
                {
                    int kHalfRobotPeriod = 20;
                    t.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, kHalfRobotPeriod, kTimeoutMs);  // Example uses 10
                    t.configMotionCruiseVelocity(g.kCruiseVelocity.get(), kTimeoutMs);
                    t.configMotionAcceleration(g.kAcceleration.get(), kTimeoutMs);
                }
                configPIDF(t, g);
            }
            talons.add(t);
        }
        // AutoTune.getInstance().AddAllTheGains(talons, config);
        return talons;
    }

    public static ArrayList<BaseMotorController> makeFollowers(BaseMotorController master, MasterConfig config)
    {
        ArrayList<BaseMotorController> followers = new ArrayList<BaseMotorController>();

        if (!config.kFollowers.isPresent())
        {
            ConsoleLogger.warning(String.format("Followers absent in master %d config", config.kAddress));
            return followers;
        }

        for (FollowerConfig c : config.kFollowers.get())
        {
            BaseMotorController f = (c.kIsTalon) ? makeFollowerTalon(c.kAddress, master) : makeFollowerVictor(c.kAddress, master);
            boolean invert = c.kInvert.orElse(config.kInvert);

            f.setInverted(invert);
            followers.add(f);
        }
        return followers;
    }

    /**
     * Create Talon that mirrors the master Talon's outputs
     * Recommended for any single sensor multiple motor subsystem. Inverted can be set if motors need to be opposites.
     * Follower will mirror voltage output of master, not independent closed loop control.
     * @param address address of new Talon
     * @param master address of master Talon
     * @return
     */
    public static TalonSRX makeFollowerTalon(int address, BaseMotorController master)
    {
        TalonSRX t = makeTalon(address);

        t.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 1000, kTimeoutMs);    // No limit switches or anything
        t.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1000, kTimeoutMs);  // No sensor position, sensor velocity, current
        t.setControlFramePeriod(ControlFrame.Control_3_General, 100);  // RIO sends SETPOINT, motor + control loop inverted, sensor phase + type, control slot, ramp rate, brake mode, limit switch override
        t.follow(master);  // Mismatched motor controller types MUST use follow(), not set()

        return t;
    }

    /**
     * Create Victor that mirrors the master Talon's outputs
     * Recommended for any single sensor multiple motor subsystem. Inverted can be set if motors need to be opposites.
     * Follower will mirror voltage output of master, not independent closed loop control.
     * @param address address of new Talon
     * @param master address of master Talon
     * @return
     */
    public static VictorSPX makeFollowerVictor(int address, BaseMotorController master)
    {
        VictorSPX t = new VictorSPX(address);

        configDefaults(t);
        t.setStatusFramePeriod(StatusFrame.Status_1_General, 1000, kTimeoutMs);    // No limit switches or anything
        t.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 1000, kTimeoutMs);  // No sensor position, sensor velocity, current
        t.setControlFramePeriod(ControlFrame.Control_3_General, 100);  // RIO sends SETPOINT, motor + control loop inverted, sensor phase + type, control slot, ramp rate, brake mode, limit switch override
        t.follow(master);  // Mismatched motor controller types MUST use follow(), not set()

        return t;
    }

    /**
     * Create Talon with SAFM default settings
     * @param address Address of new Talon
     * @return Talon with team-specific default settings. Hopefully sane!
     */
    public static TalonSRX makeTalon(int address)
    {
        TalonSRX t = new LazyTalon(address);
        configDefaults(t);
        return t;
    }

    /** Set SAFM defaults - Resets the Talon even if your code no longer calls a method :D */
    private static void configDefaults(BaseMotorController t)
    {
        // TODO Retries
        t.clearMotionProfileHasUnderrun(kTimeoutMs);           // Motion profile fault
        t.clearMotionProfileTrajectories();                    // Motion profile setpoints
        t.clearStickyFaults(kTimeoutMs);                       // Latched faults
        t.configAllowableClosedloopError(0, 0, kTimeoutMs);    // Track F if error small enough. Probably only use for speed control?
        t.configAllowableClosedloopError(1, 0, kTimeoutMs);
        t.configClosedloopRamp(0.0, kTimeoutMs);               // WARNING: Non-zero (or very small) values likely to introduce oscillations.
        t.configForwardSoftLimitEnable(false, kTimeoutMs);
        t.configForwardSoftLimitThreshold(0, kTimeoutMs);
        t.configNeutralDeadband(0.04, 10);                      // Output promoted to zero below this threshold, same effect as software deadband but also affects closed loop
        t.configNominalOutputForward(0.0, kTimeoutMs);         // Weakest output if closed loop error is non-zero, substitute for I term that doesn't cause integral wind-up. Typically 0 or positive percent.
        t.configNominalOutputReverse(0.0, kTimeoutMs);         // Weakest output if closed loop error is non-zero, substitute for I term that doesn't cause integral wind-up. Typically 0 or negative percent.
        t.configOpenloopRamp(0.0, kTimeoutMs);                 // Units: Seconds from neutral to full. WARNING: Both types apply to follower mode too. Ex: Use on high inertia hardware like shooters.
        t.configPeakOutputForward(1.0, kTimeoutMs);            // Full range
        t.configPeakOutputReverse(-1.0, kTimeoutMs);           // Full range
        t.configReverseSoftLimitEnable(false, kTimeoutMs);
        t.configReverseSoftLimitThreshold(0, kTimeoutMs);
        t.setInverted(false);                                  // Flip if sensor error working but normal motor direction incorrect
        t.setNeutralMode(NeutralMode.Coast);                   // CTRE default is brake during neutral - TODO Should we also do this?
        t.setSensorPhase(false);                               // Flip if motor direction correct but error increases without bound
        configVoltageCompensation(t);                          // Adjust output as if battery always 12V, more repeatable motion, allows more aggressive control loop tune
        LimitSwitch.configHardwareLimitAutoZero(t, false, false);
        LimitSwitch.disable(t);
        if (t instanceof TalonSRX)
        {
            TalonSRX talon = (TalonSRX) t;
            talon.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0); // Red LED blink toward M+ (white) for positive fault triggered
            talon.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0); // Red LED blink toward M- (green) for negative fault triggered
            talon.enableCurrentLimit(false);

            // Status frames get updated values FROM talon
            TalonFactory.configOpenLoopOnly(talon);
            talon.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 20, kTimeoutMs);  // Keep decent limit switch state
            talon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 25, kTimeoutMs);  // Keep modest sensor position, sensor velocity, current update rates in case subsystems needs them at all
        }
        else if (t instanceof VictorSPX)
        {
            t.setStatusFramePeriod(StatusFrame.Status_1_General, 20, kTimeoutMs);
            t.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 1000, kTimeoutMs);  // TODO Increase to 100? No encoder or current monitoring, so...
            t.setStatusFramePeriod(StatusFrame.Status_4_AinTempVbat, 1000, kTimeoutMs);
            t.setStatusFramePeriod(StatusFrame.Status_10_MotionMagic, 1000, kTimeoutMs);
            t.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 1000, kTimeoutMs);
            t.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, 1000, kTimeoutMs);
        }

        // Control frames send values TO talon
        t.setControlFramePeriod(ControlFrame.Control_3_General, 5);  // RIO sends SETPOINT, motor + control loop inverted, sensor phase + type, control slot, ramp rate, brake mode, limit switch override

        t.set(ControlMode.PercentOutput, 0.0);
    }

    private static void configEncoder(TalonSRX t)
    {
        t.setSelectedSensorPosition(0, 0, kTimeoutMs);
        t.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, kTimeoutMs);
    }

    private static void configPIDF(TalonSRX t, Gains config)
    {
        t.config_kP(config.kSlot, config.kP, kTimeoutMs);
        t.config_kI(config.kSlot, config.kI, kTimeoutMs);
        t.config_kD(config.kSlot, config.kD, kTimeoutMs);
        t.config_kF(config.kSlot, config.kF, kTimeoutMs);
        t.config_IntegralZone(config.kSlot, config.kIZone, kTimeoutMs);
        t.set(ControlMode.PercentOutput, 0.0);
    }
}
