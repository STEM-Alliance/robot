package org.wfrobotics.reuse.hardware;

import org.wfrobotics.reuse.utilities.HerdLogger;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.FeedbackDeviceStatus;
import com.ctre.CANTalon.TalonControlMode;

public abstract class CANTalonFactory
{
    public enum TALON_SENSOR
    {
        MAG_ENCODER,
    }

    public CANTalon makeVoltageControlTalon()
    {
        return null;
    }

    public static CANTalon makeSpeedControlTalon(int address, TALON_SENSOR type)
    {
        CANTalon t = new CANTalon(address);

        t.ConfigFwdLimitSwitchNormallyOpen(true);
        t.ConfigRevLimitSwitchNormallyOpen(true);
        t.enableForwardSoftLimit(false);
        t.enableReverseSoftLimit(false);
        t.enableBrakeMode(false);
        t.configNominalOutputVoltage(0, 0);  // Hardware deadband in closed-loop modes

        if (type == TALON_SENSOR.MAG_ENCODER)
        {
            t.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);  // Relative - Faster update rate, zero in absolute first
        }
        t.changeControlMode(TalonControlMode.Speed);
        t.set(0); // Set set point immediately after mode change

        FeedbackDeviceStatus s = t.isSensorPresent(FeedbackDevice.CtreMagEncoder_Relative);
        if (s != FeedbackDeviceStatus.FeedbackStatusPresent)  // CTR mag encoder & pulse width encoded only
        {
            new HerdLogger("CANTalonFactory").error("CANTalonFactory",  "Address " + Integer.toString(address) + "'s sensor not plugged in");
        }

        return t;
    }

    public CANTalon makePositionControlTalon()
    {
        return null;
    }

    /** Mirrors its master. Call set() with Master's address **/
    public CANTalon makeSlaveControlTalon(int address, int masterAddress)
    {
        CANTalon t = makeTalon(address, false);

        // Recommended for any single sensor multiple motor subsystem

        // TODO Slow settings

        t.changeControlMode(TalonControlMode.Follower);
        t.set(masterAddress);

        return t;
    }

    /** Normal CANTalon. Also documents settings. Override settings on returned talon as needed. **/
    private CANTalon makeTalon(int address, boolean hasSensors)
    {
        CANTalon t = new CANTalon(address);

        // See CTR Software Reference Manual
        // Debug              - Consider web API self-test
        // Status and signals - CANTalon periodically sends most of its state to RIO without query
        //                      Encoder, analog, batter, faults, limit switches, throttle, control mode, brakes, closed loop error, position + velocity
        // Reversing sensor   - Sensor and motor must be "in-phase", meaning positive sensor change causes positive motor movement from positive internal throttle
        //                      Move motor positive direction while logging getSensorPosition(), check positive & positive throttle (green blinking). If not, flip reverseSensor() param.
        // Velocity           - TODO CTR 7.8+

        // Set defaults - Most are CTR defaults in case CANTalon flashed wrong
        t.configMaxOutputVoltage(12);  // Full range. Haven't seen teams do otherwise.
        t.reverseOutput(false);        // Inverts the closed loop output math. Is alternative means to flip motor direction. ReverseSensor typically sufficient to keep sensor and motor in phase with limit switches and closed loop error.
        t.reverseSensor(false);        // Inverts the sensor position and sensor velocity signals. Can be used to reverse slave motor.
        t.setVoltageRampRate(0);       // Internal throttle units per 10ms cap (range: 0 - 1023). ex: 6 equates to 0V to 6V max in 1 sec despite control loop. Use on high inertia hardware like shooters.

        // Brake and limit switch settings disable CANTalon when set - these methods are slow
        t.ConfigFwdLimitSwitchNormallyOpen(true);  // Red LED blink toward M+ (white) for positive fault triggered
        t.ConfigRevLimitSwitchNormallyOpen(true);  // Red LED blink toward M- (green) for negative fault triggered
        t.enableLimitSwitch(false, false);
        t.enableBrakeMode(false);                  // When in neutral, applies brake

        t.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
        t.set(0);  // Set set point immediately after mode change

        return null;
    }
}
