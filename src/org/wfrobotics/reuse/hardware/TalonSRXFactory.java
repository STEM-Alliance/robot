package org.wfrobotics.reuse.hardware;

import org.wfrobotics.reuse.utilities.HerdLogger;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.ControlMode;

public abstract class TalonSRXFactory
{
    public enum TALON_SENSOR
    {
        MAG_ENCODER,
        ANALOG
    }

    public TalonSRX makeVoltageControlTalon()
    {
        return null;  // Not ready yet
    }

    /**
     * Create a velocity/speed controlled Talon
     * @param address address of new Talon
     * @param type type of sensor to use, generally {@link TALON_SENSOR#MAG_ENCODER}
     * @return
     */
    public static TalonSRX makeSpeedControlTalon(int address, TALON_SENSOR type)
    {
        TalonSRX t = TalonSRXFactory.makeTalon(address);

        t.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
        t.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
        t.configForwardSoftLimitEnable(false, 0);
        t.configReverseSoftLimitEnable(false, 0);
        t.setNeutralMode(NeutralMode.Coast);
        t.configNominalOutputForward(0, 0);  // Hardware deadband in closed-loop modes
        t.configNominalOutputReverse(0, 0);  // Hardware deadband in closed-loop modes

        if(type == TALON_SENSOR.MAG_ENCODER)
        {
            // Relative - Faster update rate, zero in absolute first
            t.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
        }
        
        t.set(ControlMode.Velocity, 0); // Set set point immediately after mode change

        //TODO couldn't find a replacement function
//        FeedbackDeviceStatus s = t.isSensorPresent(FeedbackDevice.CTRE_MagEncoder_Relative);
//        if (s != FeedbackDeviceStatus.FeedbackStatusPresent)  // CTR mag encoder & pulse width encoded only
//        {
//            new HerdLogger("CANTalonFactory").error("CANTalonFactory",  "Address " + Integer.toString(address) + "'s sensor not plugged in");
//        }

        return t;
    }

    /**
     * Create a Talon that is controlled by a desired angle 
     * @param address address of new Talon
     * @return
     */
    public static TalonSRX makeAngleControlTalon(int address)
    {
        TalonSRX t = makeTalon(address);

        t.configNominalOutputForward(0, 0);
        t.configNominalOutputReverse(0, 0);
        t.configPeakOutputForward(1, 0);
        t.configPeakOutputReverse(-1, 0); //TODO is this -1 or 1?
        t.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
        t.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
        t.configForwardSoftLimitEnable(false, 0);
        t.configReverseSoftLimitEnable(false, 0);
        t.setNeutralMode(NeutralMode.Coast);
        
        //t.setStatusFrameRateMs(CANTalon.StatusFrameRate.Feedback, 50);
        t.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_2Ms, 0);
        t.configVelocityMeasurementWindow(0, 0);

        return t;
    }

    /**
     * Mirror the master Talon's outputs
     * @param address address of new Talon
     * @param masterAddress address of master Talon
     * @return
     */
    public static TalonSRX makeFollowerTalon(int address, int masterAddress)
    {
        TalonSRX t = makeTalon(address);

        // Recommended for any single sensor multiple motor subsystem

        // TODO Slow settings
        //t.setStatusFramePeriod(StatusFrame.Status_1_General, 1000, 0);
        t.set(ControlMode.Follower, masterAddress);

        return t;
    }

    /**
     *  Normal TalonSRX. Also documents settings.
     *  Override settings on returned Talon as needed.
     * @param address address of new Talon
     * @return
     */
    public static TalonSRX makeTalon(int address)
    {
        TalonSRX t = new LazyTalon(address);

        // See CTR Software Reference Manual
        // Debug              - Consider web API self-test
        // Status and signals - CANTalon periodically sends most of its state to RIO without query
        //                      Encoder, analog, batter, faults, limit switches, throttle, control mode, brakes, closed loop error, position + velocity
        // Reversing sensor   - Sensor and motor must be "in-phase", meaning positive sensor change causes positive motor movement from positive internal throttle
        //                      Move motor positive direction while logging getSensorPosition(), check positive & positive throttle (green blinking). If not, flip reverseSensor() param.
        // Velocity           - TODO CTR 7.8+

        // Set defaults - Most are CTR defaults in case CANTalon flashed wrong
        t.configPeakOutputForward(1, 0);    // Full range. Haven't seen teams do otherwise.
        t.configPeakOutputReverse(-1, 0);   //TODO is this -1 or 1? 
        t.setInverted(false);               // Inverts the closed loop output math. Is alternative means to flip motor direction. ReverseSensor typically sufficient to keep sensor and motor in phase with limit switches and closed loop error.
        t.setSensorPhase(false);            // Inverts the sensor position and sensor velocity signals. Can be used to reverse slave motor.
        t.configOpenloopRamp(0, 0);         // Seconds from neutral to full -  Use on high inertia hardware like shooters.

        // Brake and limit switch settings disable CANTalon when set - these methods are slow
        t.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0); // Red LED blink toward M+ (white) for positive fault triggered
        t.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0); // Red LED blink toward M- (green) for negative fault triggered
        t.overrideLimitSwitchesEnable(false);
        t.setNeutralMode(NeutralMode.Coast);
        
        t.set(ControlMode.PercentOutput, 0);

        return t; // Not ready yet
    }
}
