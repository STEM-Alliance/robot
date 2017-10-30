package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.hardware.CANTalonFactory;
import org.wfrobotics.reuse.hardware.CANTalonFactory.TALON_SENSOR;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.RobotState;

import com.ctre.CANTalon;

/**
 * Handle motor outputs and feedback for an individual swerve wheel
 * @author Team 4818 WFRobotics
 */
public class SwerveWheel
{
    RobotState state = RobotState.getInstance();

    private final AngleMotor angleMotor;
    private final CANTalon driveMotor;

    private boolean brakeEnabled;

    public SwerveWheel(int motorDrive, AngleMotor motorAngle)
    {
        angleMotor = motorAngle;

        driveMotor = CANTalonFactory.makeSpeedControlTalon(motorDrive, TALON_SENSOR.MAG_ENCODER);
        driveMotor.setVoltageRampRate(30);
        driveMotor.setPID(Config.DRIVE_P, Config.DRIVE_I, Config.DRIVE_D, Config.DRIVE_F, 0, 10, 0);
        driveMotor.reverseSensor(true);

        setBrake(false);
    }

    public String toString()
    {
        return String.format("%.2f, %.2f\u00b0", driveMotor.getSpeed(), angleMotor.getDegrees());
    }

    public void set(HerdVector desired, boolean brake)
    {
        boolean reverseDrive = angleMotor.update(desired);  // TODO Consider moving motor reversal to swerve wheel. Is it a "wheel thing" or "angle motor thing"?
        double speed;

        if (reverseDrive)
        {
            desired = desired.scale(-1);
        }
        speed = desired.getMag() * Config.DRIVE_SPEED_MAX;  // 1 --> max RPM obtainable
        driveMotor.set(speed);

        setBrake(brake);
    }

    public void setBrake(boolean enable)
    {
        if (brakeEnabled != enable)
        {
            driveMotor.enableBrakeMode(enable);
            brakeEnabled = enable;
        }
    }
}
