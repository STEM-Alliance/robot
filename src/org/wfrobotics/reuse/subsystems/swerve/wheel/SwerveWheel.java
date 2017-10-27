package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.RobotState;

// TODO Move shifters to chassis, does make sense for each to set robot state

/**
 * Handle motor outputs and feedback for an individual swerve wheel
 * @author Team 4818 WFRobotics
 */
public class SwerveWheel
{
    private final String NAME;

    RobotState state = RobotState.getInstance();
    HerdLogger log = new HerdLogger(SwerveWheel.class);

    private final DriveMotor driveMotor;
    private final AngleMotor angleMotor;

    public SwerveWheel(String name, int addressDrive, int addressAngle)
    {
        NAME = name;

        driveMotor = new DriveMotor(addressDrive, Config.DRIVE_SPEED_SENSOR_ENABLE);
        angleMotor = new AngleMotorMagPot(NAME + ".Angle", addressAngle);
    }

    public String toString()
    {
        return String.format("%.2f, %.2f\u00b0", driveMotor.get(), angleMotor.getDegrees());
    }

    /**
     * Set the desired wheel vector, auto updates the PID controllers
     * @param desired Velocity and Rotation of this wheel
     * @param gearHigh True: High gear, False: Low gear
     * @param brake Enable brake mode?
     */
    public void set(HerdVector desired, boolean gear, boolean brake)
    {
        boolean reverseDrive = angleMotor.update(desired);  // TODO Consider moving motor reversal to swerve wheel. Is it a "wheel thing" or "angle motor thing"?

        double driveCommand = (reverseDrive) ? -desired.getMag() : desired.getMag();
        driveMotor.set(driveCommand);
        driveMotor.setBrake(brake);
        log.debug(NAME, this);
    }
}
