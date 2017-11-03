package org.wfrobotics.reuse.hardware;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
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
            t.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        }
        t.changeControlMode(TalonControlMode.Speed);

        return t;
    }

    public CANTalon makePositionControlTalon()
    {
        return null;
    }

    public CANTalon makeSlaveControlTalon()
    {
        return null;
    }
}
