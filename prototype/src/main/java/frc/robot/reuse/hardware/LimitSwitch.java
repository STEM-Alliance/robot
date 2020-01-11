package frc.robot.reuse.hardware;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.RemoteFeedbackDevice;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/** @author STEM Alliance of Fargo Moorhead */
public class LimitSwitch
{
    public enum Limit
    {
        FORWARD,
        REVERSE
    }

    private final TalonSRX motor;
    private boolean invertF;  // Adjust for limit switch normal state type
    private boolean invertR;  // Adjust for limit switch normal state type

    public LimitSwitch(TalonSRX motor)
    {
        this.motor = motor;
        invertF = false;
        invertR = false;
    }

    public LimitSwitch(TalonSRX motor, boolean normallyOpenReverse, boolean normallyOpenForward)
    {
        this.motor = motor;
        configHardwareLimitR((normallyOpenReverse) ? LimitSwitchNormal.NormallyOpen : LimitSwitchNormal.NormallyClosed);
        configHardwareLimitF((normallyOpenForward) ? LimitSwitchNormal.NormallyOpen : LimitSwitchNormal.NormallyClosed);
    }

    /** Disable motor when sensor enabled. */
    public void configHardwareLimitF(LimitSwitchNormal notSetState)
    {
        motor.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, notSetState, 100);
        motor.overrideLimitSwitchesEnable(true);  // MUST be true to enable hardware limit switch feature
        invertF = notSetState == LimitSwitchNormal.NormallyClosed;
    }

    /** Disable motor when sensor enabled. */
    public void configHardwareLimitR(LimitSwitchNormal notSetState)
    {
        motor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, notSetState, 100);
        motor.overrideLimitSwitchesEnable(true);  // MUST be true to enable hardware limit switch feature
        invertR = notSetState == LimitSwitchNormal.NormallyClosed;
    }

    /** Disable motor when sensor enabled. "Remote" means use a sensor which is not physically connected to this device. */
    public void configRemoteHardwareLimitF(LimitSwitchNormal notSetState, int sensorDeviceId, RemoteSensorSource sensor, RemoteLimitSwitchSource type)
    {
        int remoteSensor = 0;
        int pid = 0;
        motor.configRemoteFeedbackFilter(0, sensor, remoteSensor, 100);  // Assign signal to remote sensor
        motor.configSelectedFeedbackSensor(RemoteFeedbackDevice.RemoteSensor0, pid, 100);  // Assign sensor to closed loop
        motor.configForwardLimitSwitchSource(type, notSetState, sensorDeviceId, 100);  // Assign sensor as talon hardware limit
        motor.overrideLimitSwitchesEnable(true);  // MUST be true to enable hardware limit switch feature
        invertF = notSetState == LimitSwitchNormal.NormallyClosed;
    }

    /** Disable motor when sensor enabled. "Remote" means use a sensor which is not physically connected to this device. */
    public void configRemoteHardwareLimitR(LimitSwitchNormal notSetState, int sensorDeviceId, RemoteSensorSource sensor, RemoteLimitSwitchSource type)
    {
        int remoteSensor = 0;
        int pid = 0;
        motor.configRemoteFeedbackFilter(0, sensor, remoteSensor, 100);  // Assign signal to remote sensor
        motor.configSelectedFeedbackSensor(RemoteFeedbackDevice.RemoteSensor0, pid, 100);  // Assign sensor to closed loop
        motor.configReverseLimitSwitchSource(type, notSetState, sensorDeviceId, 100);  // Assign sensor as talon hardware limit
        motor.overrideLimitSwitchesEnable(true);  // MUST be true to enable hardware limit switch feature
        invertR = notSetState == LimitSwitchNormal.NormallyClosed;
    }

    /** Detect if hardware limit sensor is enabled. Boolean adjusted for limit switch normal state type. */
    public boolean isSet(Limit limit)
    {
        if(limit == Limit.REVERSE)
        {
            return motor.getSensorCollection().isRevLimitSwitchClosed() ^ invertR;
        }
        return motor.getSensorCollection().isFwdLimitSwitchClosed() ^ invertF;
    }

    /** Optional feature. Sets sensor position when at limit. */
    public static void configHardwareLimitAutoZero(BaseMotorController t, boolean zeroOnReverse, boolean zeroOnForward)
    {
        t.configSetParameter(ParamEnum.eClearPositionOnLimitR, (zeroOnReverse) ? 1 : 0, 0, 0, 10);
        t.configSetParameter(ParamEnum.eClearPositionOnLimitF, (zeroOnForward) ? 1 : 0, 0, 0, 10);
    }

    /** Allows you to ignore if inputs are in range. Good practice to enable. Use a valid slightly before any hardware limit switch. */
    public static void configSoftwareLimitF(TalonSRX t, int ticks, boolean enable)
    {
        t.configForwardSoftLimitThreshold(ticks, 100);
        t.configForwardSoftLimitEnable(enable, 100);
        t.overrideSoftLimitsEnable(true);  // Keep?
    }

    /** Allows you to ignore if inputs are in range. Good practice to enable. Use a valid slightly before any hardware limit switch. */
    public static void configSoftwareLimitR(TalonSRX t, int ticks, boolean enable)
    {
        t.configReverseSoftLimitThreshold(ticks, 100);
        t.configReverseSoftLimitEnable(enable, 100);
        t.overrideSoftLimitsEnable(true);  // Keep?
    }

    /** Use to ignore limit switches. Ex: hardware is broken, press button to swap to open loop only */
    public static void disable(BaseMotorController t)
    {
        t.overrideLimitSwitchesEnable(false);
        t.overrideSoftLimitsEnable(false);
    }

    public static boolean atReverseAll(LimitSwitch[] switches)
    {
        return allSet(switches, Limit.REVERSE);
    }

    public static boolean atForwardAll(LimitSwitch[] switches)
    {
        return allSet(switches, Limit.FORWARD);
    }

    public static boolean atReverseAny(LimitSwitch[] switches)
    {
        return anySet(switches, Limit.REVERSE);
    }

    public static boolean atForwardAny(LimitSwitch[] switches)
    {
        return anySet(switches, Limit.FORWARD);
    }

    public static boolean[][] dump(LimitSwitch[] switches)
    {
        boolean[][] buffer = new boolean[switches.length][2];

        for(int index = 0; index < switches.length; index++)
        {
            buffer[index][0] = switches[index].isSet(Limit.REVERSE);
            buffer[index][1] = switches[index].isSet(Limit.FORWARD);
        }
        return buffer;
    }

    public static boolean allSet(LimitSwitch[] switches, Limit limit)
    {
        for (int index = 0; index < switches.length; index++)
        {
            if(!switches[index].isSet(limit))
            {
                return false;
            }
        }
        return true;
    }

    public static boolean anySet(LimitSwitch[] switches, Limit limit)
    {
        for (int index = 0; index < switches.length; index++)
        {
            if(switches[index].isSet(limit))
            {
                return true;
            }
        }
        return false;
    }
}
