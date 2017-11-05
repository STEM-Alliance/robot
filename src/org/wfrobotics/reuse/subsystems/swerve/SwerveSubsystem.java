package org.wfrobotics.reuse.subsystems.swerve;

import org.wfrobotics.reuse.commands.drive.swerve.DriveSwerve;
import org.wfrobotics.reuse.hardware.sensors.Gyro;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.PIDController;
import org.wfrobotics.reuse.utilities.Utilities;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;

// TODO Always have heading
// TODO Drive mode - Common drive command by turning snapToHeading into one of two modes
// TODO remove field relative?
// TODO routine to test swerve talons
// TODO Try scaling pid output to full range (don't include deadband). Is integral limit - disable when out of range.

/**
 * Swerve Drive implementation
 * @author Team 4818 WFRobotics
 */
public class SwerveSubsystem extends Subsystem
{
    private final double ROTATION_MIN = .1;  // this will hopefully prevent locking the wheels when slowing down
    private final double HEADING_TIMEOUT = .6;

    RobotState state = RobotState.getInstance();
    private Gyro gyro = Gyro.getInstance();
    private HerdLogger log = new HerdLogger(SwerveSubsystem.class);

    private PIDController chassisAngleController;
    private Chassis wheelManager;
    private double lastHeadingTimestamp;  // Addresses counter spinning/snapback

    private boolean requestedBrake = false;
    public boolean requestedHighGear = false;  // (True: High gear, False: Low gear)

    public SwerveSubsystem()
    {
        chassisAngleController = new PIDController(Config.CHASSIS_P, Config.CHASSIS_I, Config.CHASSIS_D, 1.0);
        wheelManager = new Chassis();
        zeroGyro();
    }

    public String toString()
    {
        return String.format("Brake: %b", requestedBrake);
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new DriveSwerve());
    }

    public void driveWithHeading(SwerveSignal command)
    {
        SwerveSignal s = new SwerveSignal(command);
        double heading;

        chassisAngleController.setP(Preferences.getInstance().getDouble("SwervePID_P", Config.CHASSIS_P));
        chassisAngleController.setI(Preferences.getInstance().getDouble("SwervePID_I", Config.CHASSIS_I));
        chassisAngleController.setD(Preferences.getInstance().getDouble("SwervePID_D", Config.CHASSIS_D));

        if (command.hasHeading())
        {
            heading = command.heading;
        }
        else
        {
            heading = s.velocity.getAngle();
        }

        double error = Utilities.wrapToRange(heading + gyro.getYaw(), -180, 180);  // Make herd vector and rotate?

        log.debug("Rotation Error", error);

        ApplySpinMode(s, error);

        log.info("Chassis Command", s);

        wheelManager.update(s.velocity, s.spin, requestedHighGear, requestedBrake);
    }

    private void ApplySpinMode(SwerveSignal command, double error)
    {
        if (command.hasHeading())
        {
            command.spin = chassisAngleController.update(error);
            log.info("Drive Mode", "Snap To Angle");
        }
        else if (Math.abs(command.spin) > ROTATION_MIN)
        {
            lastHeadingTimestamp = Timer.getFPGATimestamp();  // Save off timestamp to counter snapback

            // Square rotation value to give it more control at lower values but keep the same sign since a negative squared is positive
            // TODO Does this improve anything or add complexity because we scale rotation elsewhere?
            command.spin = Math.signum(command.spin) * Math.pow(command.spin, 2);
            log.info("Drive Mode", "Rotate Angle");
        }
        else
        {
            // Delay the stay at angle to prevent snapback
            if((Timer.getFPGATimestamp() - lastHeadingTimestamp) < HEADING_TIMEOUT)
            {
                chassisAngleController.resetError();
            }
            else
            {
                double pidRotation = chassisAngleController.update(error);

                if(Math.abs(error) < 2)  // TODO: Seems incorrect
                {
                    chassisAngleController.resetError();
                }

                // Add a deadband to hopefully help with wheel lock after stopping
                log.debug("MaintainRotation", pidRotation);
                if(Math.abs(pidRotation) > ROTATION_MIN)// TODO remove unless this does anything more than talon nominal voltage
                {
                    command.spin = pidRotation;
                }
            }
            log.info("Drive Mode", "Maintain Angle");
        }
        state.updateRobotHeading(gyro.getYaw());
    }

    public void zeroGyro() { gyro.zeroYaw(); state.updateRobotHeading(gyro.getYaw()); }

    public void setBrake(boolean enable) { requestedBrake = enable; }
}
