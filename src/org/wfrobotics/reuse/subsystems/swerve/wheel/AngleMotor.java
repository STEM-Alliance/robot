package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.hardware.sensors.MagnetoPot;
import org.wfrobotics.reuse.hardware.sensors.MagnetoPotSRX;
import org.wfrobotics.reuse.utilities.HerdAngle;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

/**
 * Controls swerve wheel turning
 * @author Team 4818 WFRobotics
 */
public class AngleMotor
{
    public enum SENSOR
    {
        ENCODER,
        MAGPOT,
    }

    public interface Angle
    {
        public HerdAngle getAngle();
    }

    private final boolean ANGLE_INVERTED = false;  //Invert the angle motor and sensor to swap left/right

    private final CANTalon motor;
    private final Angle sensor;

    public AngleMotor(CANTalon motor, SENSOR type)
    {
        this.motor = motor;

        if (type == SENSOR.ENCODER)
        {
            sensor = new AngleMotorEncoder(motor, true);
        }
        else
        {
            sensor = new AngleMotorMagPot(motor, false);
        }
    }

    public String toString()
    {
        return String.format("A: %.0f\u00b0", sensor.getAngle().getAngle());
    }

    public double getDegrees()
    {
        return sensor.getAngle().getAngle();
    }

    public void set(double speed)
    {
        double invert = ANGLE_INVERTED ? -1 : 1;

        motor.set(invert * speed);
    }

    //TODO Use Talon PID for encoder controlled angle motor?
    private class AngleMotorEncoder implements Angle
    {
        private final double INVERT;  // Flip to swap left and right motors

        private final CANTalon hardware;

        public AngleMotorEncoder(CANTalon motor, boolean angleInvert)
        {
            hardware = motor;
            hardware.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
            hardware.changeControlMode(TalonControlMode.PercentVbus);
            // hardware.setPosition(angleMotor.getPosition());
            // TODO Set the initial position, switch to relative mode --> Test this, should produce 4x HW closed loop speedup

            INVERT = (angleInvert) ? -1 : 1;
        }

        public HerdAngle getAngle()
        {
            HerdAngle wrappedDegrees = new HerdAngle(hardware.getPosition() * 360);

            return new HerdAngle(INVERT * wrappedDegrees.getAngle());
        }
    }

    private class AngleMotorMagPot implements Angle
    {
        private final double INVERT;  // Flip to swap left and right motors

        private final MagnetoPot hardware;

        public AngleMotorMagPot(CANTalon motor, boolean angleInvert)
        {
            hardware = new MagnetoPotSRX(motor, 180, -180);
            INVERT = (angleInvert) ? -1 : 1;
        }

        public HerdAngle getAngle()
        {
            return new HerdAngle(INVERT * hardware.get());
        }
    }
}
