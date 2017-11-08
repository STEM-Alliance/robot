package org.wfrobotics.reuse.subsystems.swerve;

import java.util.logging.Level;

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

    private PIDController pidHeading;
    private Chassis wheelManager;
    private double lastHeadingTimestamp;  // Addresses counter spinning/snapback

    private boolean requestedBrake = false;
    public boolean requestedHighGear = false;  // (True: High gear, False: Low gear)

    public SwerveSubsystem()
    {
        pidHeading = new PIDController(Config.CHASSIS_P, Config.CHASSIS_I, Config.CHASSIS_D, 1.0);
        wheelManager = new Chassis();
        zeroGyro();
        log.setLevel(Level.FINE);
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
        double headingErrorWhenFullSpinCommanded = 45;  // TODO how much should spin twist the heading (creates error to influence PID output to yield higher w)

        HerdLogger.temp("s", command.spin);
        
        pidHeading.setP(Preferences.getInstance().getDouble("SwervePID_P", Config.CHASSIS_P));
        pidHeading.setI(Preferences.getInstance().getDouble("SwervePID_I", Config.CHASSIS_I));
        pidHeading.setD(Preferences.getInstance().getDouble("SwervePID_D", Config.CHASSIS_D));
        
        double error;
        double spin;
        
        if (command.hasHeading())
        {
            error = Utilities.wrapToRange(command.heading - gyro.getYaw(), -180, 180);  // Make herd vector and rotate?
        }
        else
        {
            // TODO determine how to apply spin all of the time, maybe move to DriveSwerve?
            error = Utilities.wrapToRange(command.heading - command.spin * headingErrorWhenFullSpinCommanded, -180, 180);  // Make herd vector and rotate?
        }
        if (command.spin != 0)
        {
            error += command.spin * headingErrorWhenFullSpinCommanded; 
        }
        spin = pidHeading.update(error);
        state.updateRobotHeading(gyro.getYaw());
 
        //ApplySpinMode(s, spin);

        log.debug("Rotation Error", error);
        log.debug("Rotation PID", spin);
        log.info("Chassis Command", s);

        wheelManager.update(s.velocity, spin, requestedHighGear, requestedBrake);
    }

    private void ApplySpinMode(SwerveSignal command, double pid)
    {
        
        if (command.hasHeading())
        {
            command.spin = pid;
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
//            if((Timer.getFPGATimestamp() - lastHeadingTimestamp) < HEADING_TIMEOUT)
//            {
//                chassisAngleController.resetError();
//            }
//            else
            {
                double pidRotation = pid;

//                if(Math.abs(error) < 2)  // TODO: Seems incorrect
//                {
//                    chassisAngleController.resetError();
//                }

                // Add a deadband to hopefully help with wheel lock after stopping
                log.debug("MaintainRotation", pidRotation);
                if(Math.abs(pidRotation) > ROTATION_MIN)// TODO remove unless this does anything more than talon nominal voltage
                {
                    command.spin = pidRotation;
                }
            }
            log.info("Drive Mode", "Maintain Angle");
        }
    }

    public void zeroGyro() { gyro.zeroYaw(); state.updateRobotHeading(gyro.getYaw()); }

    public void setBrake(boolean enable) { requestedBrake = enable; }
}
