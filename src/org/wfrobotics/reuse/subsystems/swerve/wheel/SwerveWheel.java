package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.hardware.CANTalonFactory;
import org.wfrobotics.reuse.hardware.CANTalonFactory.TALON_SENSOR;
import org.wfrobotics.reuse.subsystems.swerve.wheel.AngleSensor.AngleProvider;
import org.wfrobotics.reuse.subsystems.swerve.wheel.AngleSensor.SENSOR;
import org.wfrobotics.reuse.utilities.HerdVector;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

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
    private final TalonSRX angleMotor;
    private final TalonSRX driveMotor;

    private double angleSpeedMax = 1;
    private double angleOffset = 0;
    private double lastHighMagAngle;

    public SwerveWheel(int addressDriveMotor, int addressAngleMotor)
    {
        anglePID = new AnglePID(.01, 0, .05);
        angleMotor = CANTalonFactory.makeAngleControlTalon(addressAngleMotor);
        angleSensor = AngleSensor.makeSensor(angleMotor, SENSOR.MAGPOT);
        angleMotor.setInverted(false);  // TODO config file
        angleMotor.configOpenloopRamp(0, 0);
        
        driveMotor = CANTalonFactory.makeSpeedControlTalon(addressDriveMotor, TALON_SENSOR.MAG_ENCODER);
        driveMotor.config_kP(0, Config.DRIVE_P, 0);
        driveMotor.config_kI(0, Config.DRIVE_I, 0);
        driveMotor.config_kD(0, Config.DRIVE_D, 0);
        driveMotor.configOpenloopRamp(.5, 0);  // TODO Needing this probably means our PID is not being used correctly. Number very low.
        driveMotor.setSensorPhase(true);
        
        lastHighMagAngle = 0;
    }

    public String toString()
    {
        return String.format("S:%.2f, A: %.0f\u00b0, E: %s", (float)driveMotor.getSelectedSensorVelocity(0), angleSensor.getAngle(), anglePID);
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
        angleMotor.set(ControlMode.PercentOutput, angleSpeed);
        
        if (anglePID.isReverseAngleCloser())  // Smart turn 180 degrees at most
        {
            driveSpeed *= -1;
        }
        //driveMotor.set(driveSpeed);
    }

    public void setBrake(boolean enable)
    {
        driveMotor.setNeutralMode(enable ? NeutralMode.Brake : NeutralMode.Coast);
    }

    public void updateSettings(double p, double i, double d, double angleSpeedMax, double angleOffsetCal)
    {
        anglePID.updatePID(p, i, d);
        this.angleSpeedMax = angleSpeedMax;
        angleOffset = angleOffsetCal;
    }
}
