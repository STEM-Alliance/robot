package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.hardware.CANTalonFactory;
import org.wfrobotics.reuse.hardware.CANTalonFactory.TALON_SENSOR;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.RobotState;

import com.ctre.CANTalon;

// TODO Try scaling pid output of drive motor to full range (don't include deadband). Is integral limit - disable when out of range.

/**
 * Handle motor outputs and feedback for an individual swerve wheel
 * @author Team 4818 WFRobotics
 */
public class SwerveWheel
{
    RobotState state = RobotState.getInstance();

    private final AngleMotor angleMotor;
    private final CANTalon driveMotor;

    public SwerveWheel(int motorDrive, AngleMotor motorAngle)
    {
        angleMotor = motorAngle;

        driveMotor = CANTalonFactory.makeSpeedControlTalon(motorDrive, TALON_SENSOR.MAG_ENCODER);
        driveMotor.setVoltageRampRate(30);  // TODO Needing this probably means our PID is not being used correctly
        driveMotor.setPID(Config.DRIVE_P, Config.DRIVE_I, Config.DRIVE_D, Config.DRIVE_F, 0, 10, 0);
        driveMotor.reverseSensor(true);
    }

    public String toString()
    {
        return String.format("%.2f, %.1f\u00b0", driveMotor.getSpeed(), angleMotor.getDegrees());
    }

    public void set(HerdVector desired, double wheelOffsetCal)
    {
        boolean reverseDrive = angleMotor.update(desired, wheelOffsetCal);  // TODO Consider moving motor reversal to swerve wheel. Is it a "wheel thing" or "angle motor thing"?
        double speed = desired.getMag() * Config.DRIVE_SPEED_MAX;  // 1 --> max RPM obtainable

        if (reverseDrive)
        {
            speed *= -1;
        }
        driveMotor.set(speed);
    }

    public void setBrake(boolean enable)
    {
        driveMotor.enableBrakeMode(enable);
    }
}
