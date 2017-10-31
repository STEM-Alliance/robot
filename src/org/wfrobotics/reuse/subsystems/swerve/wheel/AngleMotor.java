package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Preferences;

// TODO Consider owning the specific sensor, rather than subclassing this

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
        anglePID = new AnglePID();
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
        return String.format("A: %.2f\u00b0, E: %.2f\u00b0", getDegrees(), anglePID.error);
    }

    /**
     * Update the angle motor based on the desired angle called from updateTask()
     * @return Whether the drive motor should run in the opposite direction
     */
    public boolean update(HerdVector desired)
    {
        double angleOffset = Preferences.getInstance().getDouble(NAME + ".Offset", 0);

        setSensorOffset(angleOffset);

        if (desired.getMag() > Config.DEADBAND_MINIMUM_SPEED)
        {
            double setpoint = desired.getAngle();
            double current = getDegrees();
            double angleMaxSpeed = Preferences.getInstance().getDouble("maxRotationSpeed", Config.ANGLE_MAX_SPEED);

            anglePID.update(setpoint, current);
            set(anglePID.getMotorSpeed() * angleMaxSpeed);
        }
        else
        {
            anglePID.resetIntegral();
            set(0);
        }

        log.debug(NAME, this);

        return anglePID.isReverseMotor();
    }

    public void set(double speed)
    {
        double invert = angleInverted ? -1 : 1;
        motor.set(invert * speed);
    }

    protected double round(double d, int res)
    {
        int x = (int) Math.pow(10, res);
        return Math.rint(d * x) / x;
    }
}
