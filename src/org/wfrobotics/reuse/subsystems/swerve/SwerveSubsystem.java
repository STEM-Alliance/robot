package org.wfrobotics.reuse.subsystems.swerve;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.reuse.commands.drive.DriveSwerve;
import org.wfrobotics.reuse.subsystems.DriveSubsystem;
import org.wfrobotics.reuse.subsystems.swerve.chassis.Constants;
import org.wfrobotics.reuse.subsystems.swerve.chassis.SwerveChassis;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.reuse.utilities.PIDController;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Swerve chassis implementation
 * @author Team 4818 WFRobotics
 */
public class SwerveSubsystem extends DriveSubsystem
{    
    public class ChassisCommand 
    {
        HerdVector velocity;  // Direction
        double spin;      // Twist while moving
        double heading;   // Angle to track

        public ChassisCommand(HerdVector velocity,  double rotationalSpeed, double heading)
        {
            this.velocity = new HerdVector(velocity);
            this.spin = rotationalSpeed;
            this.heading = heading;
        }

        public ChassisCommand(HerdVector velocity,  double rotationalSpeed)
        {
            this(velocity, rotationalSpeed, Double.NEGATIVE_INFINITY);
        }
        
        public String toString()
        {
//            return String.format("(V: %s, R: %.2f, H: %.2", velocity, spin, heading);
            return "This got commented out!";
        }
    }
    
    public class SwerveConfiguration
    {
        public boolean gearHigh = false;  // (True: High gear, False: Low gear)
        public boolean gyroEnable = true;

        private final double AUTO_ROTATION_MIN = .1;  // this will hopefully prevent locking the wheels when slowing down
        private final double HEADING_TIMEOUT = .6;
    }
    
    public static final double HEADING_IGNORE = -1;

    private HerdLogger log = new HerdLogger(SwerveSubsystem.class);
    public SwerveConfiguration configSwerve;
    private PIDController chassisAngleController;
    public SwerveChassis wheelManager;
    private ScheduledExecutorService scheduler;
    
    private double lastHeadingTimestamp;  // Addresses counter spinning/snapback
    double lastGyro;

    public SwerveSubsystem()
    {        
        super(true);  // set it into field relative by default
        
        configSwerve = new SwerveConfiguration();
        chassisAngleController = new PIDController(Constants.CHASSIS_P, Constants.CHASSIS_I, Constants.CHASSIS_D, 1.0);
        
        scheduler = Executors.newScheduledThreadPool(1);
        wheelManager = new SwerveChassis();
        scheduler.scheduleAtFixedRate(wheelManager, 1, 5, TimeUnit.MILLISECONDS);
    }

    public void initDefaultCommand() 
    {
        setDefaultCommand(new DriveSwerve(DriveSwerve.MODE.HALO));
    }

    @Override
    public void driveTank(double right, double left) { }

    @Override
    public void driveVector(Vector velocity, double rotation)
    {
        driveWithHeading(velocity, rotation, HEADING_IGNORE);
    }

    /**
     * Main drive update function, allows for xy movement, yaw rotation, and turn to angle/heading
     * @param velocity Direction the robot should move
     * @param rotation How fast to spin while moving (range: -1 to 1)
     * @param heading Auto-goto this angle (range: anything in degrees, zero is forward, -1 to disable)
     * @return actual wheel readings
     */
    public Vector[] driveWithHeading(Vector velocity, double rotation, double heading)
    {
        HerdVector v = new HerdVector(velocity.getMag(), velocity.getAngle());
        ChassisCommand command = new ChassisCommand(v, rotation, heading);

        chassisAngleController.setP(Preferences.getInstance().getDouble("SwervePID_P", Constants.CHASSIS_P));
        chassisAngleController.setI(Preferences.getInstance().getDouble("SwervePID_I", Constants.CHASSIS_I));
        chassisAngleController.setD(Preferences.getInstance().getDouble("SwervePID_D", Constants.CHASSIS_D));
        
        ApplySpinMode(command, heading == HEADING_IGNORE); // Pass by reference
        log.info("Chassis Command", command);

        if (m_fieldRelative)
        {
            command.velocity.rotate(m_gyro.getYaw());
        }
        
        printDash();
        
        return wheelManager.setWheelVectors(command.velocity, command.spin, configSwerve.gearHigh, m_brake);
    }
    
    private void ApplySpinMode(ChassisCommand command, boolean ignoreHeading)
    {
        double error = 0;
        String driveMode;

        if (!ignoreHeading)
        {
            driveMode = "Snap To Angle";

            if(configSwerve.gyroEnable)
            {
                error = Utilities.wrapToRange(command.heading - m_gyro.getYaw(), -180, 180);
                command.spin = chassisAngleController.update(-error);
            }
            m_lastHeading = command.heading;
        }
        else if (Math.abs(command.spin) > .1)
        {
            driveMode = "Rotate Angle";

            m_lastHeading = m_gyro.getYaw();
            lastHeadingTimestamp = Timer.getFPGATimestamp();  // Save off timestamp to counter snapback
            lastGyro = m_gyro.getYaw();

            // Square rotation value to give it more control at lower values but keep the same sign since a negative squared is positive
            // TODO Does this improve anything or add complexity because we scale rotation elsewhere?
            command.spin = Math.signum(command.spin) * Math.pow(command.spin, 2);
        }
        else
        {
            driveMode = "Maintain Angle";
            
            // Delay the stay at angle to prevent snapback
            if((Timer.getFPGATimestamp() - lastHeadingTimestamp) > configSwerve.HEADING_TIMEOUT)
            {
                if(configSwerve.gyroEnable)
                {
                    // Set the rotation using a PID controller based on current robot heading and new desired heading
                    error = -Utilities.wrapToRange(m_lastHeading - m_gyro.getYaw(), -180, 180);
                    double tempRotation = chassisAngleController.update(error);  // TODO why does sign differ from snap to angle?
                    
                    if(Math.abs(error) < 2)  // TODO: Seems incorrect
                    {
                        chassisAngleController.resetError();
                    }

                    // Add a deadband to hopefully help with wheel lock after stopping 
                    SmartDashboard.putNumber("MaintainRotation", tempRotation);
                    if(Math.abs(tempRotation) > configSwerve.AUTO_ROTATION_MIN)// TODO remove unless this does anything more than talon nominal voltage
                    {
                        command.spin = tempRotation;
                    }
                }
            }
            else
            {
                chassisAngleController.resetError();
                m_lastHeading = m_gyro.getYaw(); // Save off the latest heading until the timeout
            }
        }
        
        log.info("Drive Mode", driveMode);
        log.debug("Rotation Error", error);
    }

    /**
     * Get the last heading used for the robot. If free spinning, this will constantly update
     * @return angle in degrees of the robot heading, relative to the field
     */
    public double getLastHeading()
    {
        return m_lastHeading;
    }

    /**
     * Get the last movement vector of the robot, relative to the robot heading.
     * the adjustment for field relative mode, if applicable, has already been taken into consideration
     * @return movement vector relative to the robot heading
     */
    public Vector getLastVector()
    {
        HerdVector hv = wheelManager.getLastVector();
        return new Vector(hv.getX(), hv.getY());
    }
    
    @Override
    public void printDash()
    {
        super.printDash();
        wheelManager.printDash();

        log.info("Gyro Enabled", configSwerve.gyroEnable);
        log.info("High Gear", configSwerve.gearHigh);
    }
}
