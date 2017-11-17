package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.hardware.CANTalonFactory;
import org.wfrobotics.reuse.hardware.CANTalonFactory.TALON_SENSOR;
import org.wfrobotics.reuse.subsystems.swerve.wheel.AngleSensor.AngleProvider;
import org.wfrobotics.reuse.subsystems.swerve.wheel.AngleSensor.SENSOR;
import org.wfrobotics.reuse.utilities.HerdVector;

import com.ctre.CANTalon;

// TODO Try scaling pid output of drive motor to full range (don't include deadband). Is integral limit - disable when out of range.
// TODO Try scaling pid output of angle motor to full range (don't include deadband). Is integral limit - disable when out of range.
// TODO Try feedforward on drive speed

/**
 * Handle motor outputs and feedback for an individual swerve wheel
 * @author Team 4818 WFRobotics
 */
public class SwerveWheel
{
    private final AnglePID anglePID;
    private final AngleProvider angleSensor;
    private final CANTalon angleMotor;
    private final CANTalon driveMotor;

    private double angleSpeedMax = 1;
    private double angleOffset = 0;
    private double lastHighMagAngle;

    public SwerveWheel(int addressDriveMotor, int addressAngleMotor)
    {
        anglePID = new AnglePID(0, 0, 0);
        angleMotor = CANTalonFactory.makeAngleControlTalon(addressAngleMotor);
        angleSensor = AngleSensor.makeSensor(angleMotor, SENSOR.MAGPOT);
        angleMotor.setInverted(false);  // TODO config file
        
        driveMotor = CANTalonFactory.makeSpeedControlTalon(addressDriveMotor, TALON_SENSOR.MAG_ENCODER);
        driveMotor.setPID(Config.DRIVE_P, Config.DRIVE_I, Config.DRIVE_D);
        driveMotor.setVoltageRampRate(30);  // TODO Needing this probably means our PID is not being used correctly. Number very low.
        driveMotor.reverseSensor(true);
        lastHighMagAngle = 0;
    }

    public String toString()
    {
        return String.format("S:%.2f, A: %.0f\u00b0, E: %s", driveMotor.getSpeed(), angleSensor.getAngle(), anglePID);
    }

    public void set(HerdVector desired)
    {
        double driveSpeed = desired.getMag() * Config.DRIVE_SPEED_MAX;  // 1 --> max RPM obtainable
        double angleSetPoint = desired.rotate(angleOffset).getAngle();
        double angleCurrent = angleSensor.getAngle();
        double angleSpeed = anglePID.update(angleSetPoint, angleCurrent);
                
        if (desired.getMag() < Config.DEADBAND_STOP_TURNING_AT_REST)
        {
            angleSetPoint = lastHighMagAngle;
        }
        else
        {
            lastHighMagAngle = desired.getAngle();
        }
        angleSpeed = anglePID.update(angleSetPoint, angleCurrent);
        
        //if (desired.getMag() < Config.DEADBAND_STOP_TURNING_AT_REST)
        {
            //anglePID.resetIntegral();  // TODO Removed. Seems wrong. Better as good IZone if anything
//            angleSpeed = 0;
        }
        angleMotor.set(angleSpeed);

        if (anglePID.isReverseAngleCloser())  // Smart turn 180 degrees at most
        {
            driveSpeed *= -1;
        }
        //driveMotor.set(driveSpeed);
    }

    public void setBrake(boolean enable)
    {
        driveMotor.enableBrakeMode(enable);
    }

    public void updateSettings(double p, double i, double d, double angleSpeedMax, double angleOffsetCal)
    {
        anglePID.updatePID(p, i, d);
        this.angleSpeedMax = angleSpeedMax;
        angleOffset = angleOffsetCal;
    }
}
