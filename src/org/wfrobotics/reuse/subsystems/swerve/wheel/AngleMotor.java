package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.hardware.sensors.MagnetoPot;
import org.wfrobotics.reuse.hardware.sensors.MagnetoPotSRX;
import org.wfrobotics.reuse.utilities.HerdAngle;
import org.wfrobotics.reuse.utilities.HerdVector;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.Preferences;

// TODO Why aren't we updating the PID even when within the deadband?
// TODO Consider owning the specific sensor, rather than subclassing this
// TODO Try scaling pid output to full range (don't include deadband). Is integral limit - disable when out of range.

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

    private final AnglePIDUpdater pid;
    private final CANTalon motor;
    private final Angle sensor;

    public AngleMotor(int talonAddress, SENSOR type)
    {
        pid = new AnglePIDUpdater();
        motor = new CANTalon(talonAddress);
        motor.configNominalOutputVoltage(0, 0);
        motor.configPeakOutputVoltage(11, -11);
        motor.ConfigFwdLimitSwitchNormallyOpen(true);
        motor.ConfigRevLimitSwitchNormallyOpen(true);
        motor.enableForwardSoftLimit(false);
        motor.enableReverseSoftLimit(false);
        motor.enableBrakeMode(false);

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
        return String.format("A: %.0f\u00b0, E: %.0f\u00b0", sensor.getAngle(), pid.getError());
    }

    /**
     * Update the angle motor based on the desired angle called from updateTask()
     * @return Whether the drive motor should run in the opposite direction
     */
    public boolean update(HerdVector desired, double wheelOffsetCal)
    {
        double angleMaxSpeed = Preferences.getInstance().getDouble("maxRotationSpeed", Config.ANGLE_MAX_SPEED);
        double p = Preferences.getInstance().getDouble("WheelAngle_P", Config.ANGLE_P);
        double i = Preferences.getInstance().getDouble("WheelAngle_I", Config.ANGLE_I);
        double d = Preferences.getInstance().getDouble("WheelAngle_D", Config.ANGLE_D);
        double setpoint;
        HerdAngle current;
        double pidSpeed;
        double speed;

        setpoint = desired.rotate(wheelOffsetCal).getAngle();
        current = sensor.getAngle();

        pid.setPID(p, i, d);

        pidSpeed = pid.update(setpoint, current);
        speed = pidSpeed * angleMaxSpeed;

        if (desired.getMag() < Config.DEADBAND_MINIMUM_SPEED)
        {
            //anglePID.resetIntegral();  // TODO Removed. Seems wrong. Do we want this?
            speed = 0;
        }

        set(speed);

        return pid.reverseCloser();
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

    private class AnglePIDUpdater
    {
        private final AnglePID anglePID;

        public AnglePIDUpdater()
        {
            anglePID = new AnglePID(Config.ANGLE_P, Config.ANGLE_I, Config.ANGLE_D, 1);
        }

        public double update(double setpoint, HerdAngle current)
        {
            return anglePID.update(setpoint, current.getAngle());
        }

        public double getError()
        {
            return anglePID.errorShortestPath;
        }

        public void setPID(double p, double i, double d)
        {
            anglePID.updatePID(p, i, d);
        }

        public boolean reverseCloser()
        {
            return anglePID.reverseDriveMotorMoreEfficient();
        }
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
