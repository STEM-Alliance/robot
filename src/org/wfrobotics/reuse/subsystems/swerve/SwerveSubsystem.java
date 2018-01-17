package org.wfrobotics.reuse.subsystems.swerve;

import org.wfrobotics.reuse.commands.holonomic.DriveSwerve;
import org.wfrobotics.reuse.hardware.sensors.Gyro;
import org.wfrobotics.reuse.subsystems.drive.HolonomicDrive;
import org.wfrobotics.reuse.utilities.HerdAngle;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.reuse.utilities.PIDController;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;

// TODO change pid to AnglePID - We want chassis to spin in shortest path to heading, handling -180 to 180 transition. Rename to PolarPID?
// TODO Drive mode - Common drive command by turning snapToHeading into one of two modes
// TODO routine to test swerve talons
// TODO Try scaling pid output to full range (don't include deadband). Is integral limit - disable when out of range.

/**
 * Swerve Drive implementation
 * @author Team 4818 WFRobotics
 */
public class SwerveSubsystem extends Subsystem implements HolonomicDrive
{
    private static SwerveSubsystem instance = null;
    Preferences prefs = Preferences.getInstance();
    RobotState state = RobotState.getInstance();
    private Gyro gyro = Gyro.getInstance();
    private HerdLogger log = new HerdLogger(SwerveSubsystem.class);

    private PIDController pidHeading;
    private Chassis chassis;
    private Shifter shifters;

    private boolean brakeLastSet;
    private boolean gearLastSet;

    private SwerveSubsystem()
    {
        pidHeading = new PIDController(Config.CHASSIS_P, Config.CHASSIS_I, Config.CHASSIS_D, 1.0);
        chassis = new Chassis();
        shifters = new Shifter();

        zeroGyro();
        brakeLastSet = false;
        gearLastSet = false;
    }

    public static SwerveSubsystem getInstance()
    {
        if (instance == null) { instance = new SwerveSubsystem(); }
        return instance;
    }

    public String toString()
    {
        return String.format("Brake: %b", brakeLastSet);
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new DriveSwerve());
    }

    public void driveBasic(HerdVector vector)
    {
        DriverStation.reportWarning("TODO", true);
    }

    public void turnBasic(HerdVector vector)
    {
        DriverStation.reportWarning("TODO", true);
    }

    public void driveWithHeading(SwerveSignal command)
    {
        HerdAngle error = new HerdAngle(0);
        double pidOut;
        double spin;

        pidHeading.setP(prefs.getDouble("SwervePID_P", Config.CHASSIS_P));
        pidHeading.setI(prefs.getDouble("SwervePID_I", Config.CHASSIS_I));
        pidHeading.setD(prefs.getDouble("SwervePID_D", Config.CHASSIS_D));

        if (command.hasHeading())
        {
            error = command.heading.rotate(-gyro.getYaw());
        }
        pidOut = pidHeading.update(error.getAngle());
        spin = (command.hasHeading()) ? pidOut : command.spin;

        state.updateRobotHeading(gyro.getYaw());
        state.updateRobotGear(shifters.isHighGear());

        log.debug("Rotation Error", error);
        log.debug("Rotation PID", spin);
        log.info("Chassis Command", command);

        chassis.update(command.velocity, spin);
    }

    public void zeroGyro()
    {
        gyro.zeroYaw();
        state.updateRobotHeading(gyro.getYaw());
    }

    public void setBrake(boolean enable)
    {
        if (brakeLastSet != enable)
        {
            chassis.updateBrake(enable);
            brakeLastSet = enable;
        }
    }

    public void setGear(boolean useHighGear)
    {
        if (gearLastSet != useHighGear)
        {
            shifters.setGear(useHighGear);
            gearLastSet = useHighGear;
        }
    }
}
