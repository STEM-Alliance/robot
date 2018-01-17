package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.hardware.sensors.MagnetoPot;
import org.wfrobotics.reuse.hardware.sensors.MagnetoPotSRX;
import org.wfrobotics.reuse.utilities.HerdAngle;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.ControlMode;

/**
 * Controls swerve wheel turning
 * @author Team 4818 WFRobotics
 */
public abstract class AngleSensor
{
    public enum SENSOR
    {
        ENCODER,
        MAGPOT,
    }

    public interface AngleProvider
    {
        public double getAngle();
    }

    public static AngleProvider makeSensor(TalonSRX motor, SENSOR type)
    {
        if (type == SENSOR.ENCODER)
        {
            return new AngleMotorEncoder(motor, true);
        }
        return new AngleMotorMagPot(motor, false);
    }

    //TODO Use Talon PID for encoder controlled angle motor?
    private static class AngleMotorEncoder implements AngleProvider
    {
        private final double INVERT;  // Flip to swap left and right motors

        private final TalonSRX hardware;

        public AngleMotorEncoder(TalonSRX motor, boolean angleInvert)
        {
            hardware = motor;
            hardware.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
            hardware.set(ControlMode.PercentOutput, 0);
            // hardware.setPosition(angleMotor.getPosition());
            // TODO Set the initial position, switch to relative mode --> Test this, should produce 4x HW closed loop speedup

            INVERT = (angleInvert) ? -1 : 1;
        }

        public double getAngle()
        {
            HerdAngle wrappedDegrees = new HerdAngle(hardware.getSelectedSensorPosition(0) * 360);

            return new HerdAngle(INVERT * wrappedDegrees.getAngle()).getAngle();
        }
    }

    private static class AngleMotorMagPot implements AngleProvider
    {
        private final double INVERT;  // Flip to swap left and right motors

        private final MagnetoPot hardware;

        public AngleMotorMagPot(TalonSRX motor, boolean angleInvert)
        {
            hardware = new MagnetoPotSRX(motor, 180, -180);
            INVERT = (angleInvert) ? -1 : 1;
        }

        public double getAngle()
        {
            return new HerdAngle(INVERT * hardware.get()).getAngle();
        }
    }
}
