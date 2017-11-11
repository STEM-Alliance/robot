package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Preferences;

// TODO Why aren't we updating the PID even when within the deadband?
// TODO Consider owning the specific sensor, rather than subclassing this
// TODO Try scaling pid output to full range (don't include deadband). Is integral limit - disable when out of range.

/**
 * Controls swerve wheel turning
 * @author Team 4818 WFRobotics
 */
public abstract class AngleMotor
{
    /** Invert the angle motor and sensor to swap left/right */
    protected boolean angleInverted = false;
    protected final String NAME;

    private HerdLogger log = new HerdLogger(AngleMotor.class);
    private final AnglePID anglePID;
    protected final CANTalon motor;

    public AngleMotor(String name, int talonAddress)
    {
        NAME = name;
        anglePID = new AnglePID(Config.ANGLE_P, Config.ANGLE_I, Config.ANGLE_D, 1);
        motor = new CANTalon(talonAddress);
        motor.configNominalOutputVoltage(0, 0);
        motor.configPeakOutputVoltage(11, -11);
        motor.ConfigFwdLimitSwitchNormallyOpen(true);
        motor.ConfigRevLimitSwitchNormallyOpen(true);
        motor.enableForwardSoftLimit(false);
        motor.enableReverseSoftLimit(false);
        motor.enableBrakeMode(false);
    }

    public abstract double getDegrees();
    public abstract void setSensorOffset(double degrees);

    public String toString()
    {
        return String.format("A: %.0f\u00b0, E: %.0f\u00b0", getDegrees(), anglePID.errorShortestPath);
    }

    /**
     * Update the angle motor based on the desired angle called from updateTask()
     * @return Whether the drive motor should run in the opposite direction
     */
    public boolean update(HerdVector desired, double wheelOffsetCal)
    {
        setSensorOffset(wheelOffsetCal);

        if (desired.getMag() > Config.DEADBAND_MINIMUM_SPEED)
        {
            double angleMaxSpeed = Preferences.getInstance().getDouble("maxRotationSpeed", Config.ANGLE_MAX_SPEED);
            double p = Preferences.getInstance().getDouble("WheelAngle_P", Config.ANGLE_P);
            double i = Preferences.getInstance().getDouble("WheelAngle_I", Config.ANGLE_I);
            double d = Preferences.getInstance().getDouble("WheelAngle_D", Config.ANGLE_D);

            double setpoint = desired.getAngle();
            double current = getDegrees();
            double pidSpeed;

            anglePID.updatePID(p, i, d);
            pidSpeed = anglePID.update(setpoint, current);
            set(pidSpeed * angleMaxSpeed);
        }
        else
        {
            anglePID.resetIntegral();
            set(0);
        }

        log.debug(NAME, this);
        log.debug("Swerve AnglePID", anglePID);

        return anglePID.reverseDriveMotorMoreEfficient();
    }

    public void set(double speed)
    {
        double invert = angleInverted ? -1 : 1;
        motor.set(invert * speed);
    }
}
