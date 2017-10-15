package org.wfrobotics.reuse.subsystems.swerve;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.wfrobotics.reuse.commands.drive.swerve.DriveSwerve;
import org.wfrobotics.reuse.hardware.sensors.Gyro;
import org.wfrobotics.reuse.subsystems.swerve.chassis.Chassis;
import org.wfrobotics.reuse.subsystems.swerve.chassis.Config;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.PIDController;
import org.wfrobotics.reuse.utilities.Utilities;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Swerve Drive implementation
 * @author Team 4818 WFRobotics
 */
public class SwerveSubsystem extends Subsystem
{
    public class SwerveConfiguration
    {
        public boolean gearHigh = false;  // (True: High gear, False: Low gear)
        public boolean gyroEnable = true;

        private final double AUTO_ROTATION_MIN = .1;  // this will hopefully prevent locking the wheels when slowing down
        private final double HEADING_TIMEOUT = .6;
    }

    RobotState state = RobotState.getInstance();
    private HerdLogger log = new HerdLogger(SwerveSubsystem.class);
    public SwerveConfiguration configSwerve;
    private PIDController chassisAngleController;
    public Chassis wheelManager;
    private ScheduledExecutorService scheduler;

    private double lastHeadingTimestamp;  // Addresses counter spinning/snapback

    protected boolean fieldRelative = true;
    protected boolean brake = false;
    protected Gyro gyro;

    public SwerveSubsystem()
    {
        try
        {
            gyro = Gyro.getInstance();
        }
        catch (RuntimeException ex)
        {
            log.error("Error instantiating navX MXP: " + ex.getMessage(), true);
        }
        finally
        {
            zeroGyro();
        }

        configSwerve = new SwerveConfiguration();
        chassisAngleController = new PIDController(Config.CHASSIS_P, Config.CHASSIS_I, Config.CHASSIS_D, 1.0);

        scheduler = Executors.newScheduledThreadPool(1);
        wheelManager = new Chassis();
        scheduler.scheduleAtFixedRate(wheelManager, 1, 5, TimeUnit.MILLISECONDS);
    }

    public String toString()
    {
        return String.format("Gyro: %.2f, Heading: %.2f, field Relative: %b, Brake: %b", gyro.getYaw(), state.robotHeading, fieldRelative, brake);
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new DriveSwerve(DriveSwerve.MODE.HALO));
    }

    public void driveWithHeading(SwerveSignal command)
    {
        SwerveSignal s = new SwerveSignal(command);

        chassisAngleController.setP(Preferences.getInstance().getDouble("SwervePID_P", Config.CHASSIS_P));
        chassisAngleController.setI(Preferences.getInstance().getDouble("SwervePID_I", Config.CHASSIS_I));
        chassisAngleController.setD(Preferences.getInstance().getDouble("SwervePID_D", Config.CHASSIS_D));

        ApplySpinMode(s, s.hasHeading()); // Pass by reference
        log.info("Chassis Command", s);

        if (fieldRelative)
        {
            s.velocity.rotate(gyro.getYaw());
        }

        printDash();

        wheelManager.setWheelVectors(s.velocity, s.spin, configSwerve.gearHigh, brake);
    }

    private void ApplySpinMode(SwerveSignal command, boolean snapToHeading)
    {
        double error = 0;
        String driveMode;

        if (snapToHeading)
        {
            driveMode = "Snap To Angle";

            if(configSwerve.gyroEnable)
            {
                error = Utilities.wrapToRange(command.heading - gyro.getYaw(), -180, 180);
                command.spin = chassisAngleController.update(-error);
            }
            state.updateRobotHeading(command.heading);
        }
        else if (Math.abs(command.spin) > .1)
        {
            driveMode = "Rotate Angle";

            state.updateRobotHeading(gyro.getYaw());
            lastHeadingTimestamp = Timer.getFPGATimestamp();  // Save off timestamp to counter snapback

            // Square rotation value to give it more control at lower values but keep the same sign since a negative squared is positive
            // TODO Does this improve anything or add complexity because we scale rotation elsewhere?
            command.spin = Math.signum(command.spin) * Math.pow(command.spin, 2);
        }
        else
        {
            driveMode = "Maintain Angle";

            // Delay the stay at angle to prevent snapback
            if((Timer.getFPGATimestamp() - lastHeadingTimestamp) < configSwerve.HEADING_TIMEOUT)
            {
                chassisAngleController.resetError();
                state.updateRobotHeading(gyro.getYaw()); // Until the timeout
            }
            else if(configSwerve.gyroEnable)
            {
                error = Utilities.wrapToRange(state.robotHeading - gyro.getYaw(), -180, 180);
                double pidRotation = chassisAngleController.update(-error);

                if(Math.abs(error) < 2)  // TODO: Seems incorrect
                {
                    chassisAngleController.resetError();
                }

                // Add a deadband to hopefully help with wheel lock after stopping
                log.debug("MaintainRotation", pidRotation);
                if(Math.abs(pidRotation) > configSwerve.AUTO_ROTATION_MIN)// TODO remove unless this does anything more than talon nominal voltage
                {
                    command.spin = pidRotation;
                }
            }
        }

        log.info("Drive Mode", driveMode);
        log.debug("Rotation Error", error);
    }

    public void printDash()
    {
        log.info("Drive", this);
        log.info("Gyro Enabled", configSwerve.gyroEnable);
        log.info("High Gear", configSwerve.gearHigh);
    }

    public void setFieldRelative(boolean enable) { fieldRelative = enable; }
    public boolean getFieldRelative() { return fieldRelative; }

    public void zeroGyro(float offsetYaw) { gyro.setZero(offsetYaw); state.updateRobotHeading(gyro.getYaw()); }
    public void zeroGyro() { gyro.zeroYaw(); state.updateRobotHeading(gyro.getYaw()); }

    public void setBrake(boolean enable) { brake = enable; }
}
