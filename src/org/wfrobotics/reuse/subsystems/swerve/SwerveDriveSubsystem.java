package org.wfrobotics.reuse.subsystems.swerve;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.reuse.commands.drive.DriveSwerve;
import org.wfrobotics.reuse.subsystems.DriveSubsystem;
import org.wfrobotics.reuse.utilities.PIDController;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Swerve chassis implementation 
 * @author Team 4818 WFRobotics
 */
public class SwerveDriveSubsystem extends DriveSubsystem 
{
    public class ChassisVector
    {
        Vector velocity;  // Direction
        double spin;      // Twist while moving
        double heading;   // Angle to track

        public ChassisVector(Vector velocity,  double rotationalSpeed, double heading)
        {
            this.velocity = velocity;
            this.spin = rotationalSpeed;
            this.heading = heading;
        }

        public ChassisVector(Vector velocity,  double rotationalSpeed)
        {
            this(velocity, rotationalSpeed, HEADING_IGNORE);
        }

        public boolean ignoreHeading()
        {
            return heading == HEADING_IGNORE;
        }
        
        public ChassisVector clone()
        {
            return new ChassisVector(velocity, spin, heading);
        }
    }
    
    public class SwerveConfiguration
    {
        /**
         * Sets shifter to high or low gear (True: High gear, False: Low gear)
         */
        public boolean gearHigh = false;
        public boolean gyroEnable = true;

        private final double AUTO_ROTATION_MIN = .1;  // this will hopefully prevent locking the wheels when slowing down
        private final double HEADING_TIMEOUT = .6;
    }

    public static final double HEADING_IGNORE = -1;

    public SwerveConfiguration configSwerve;
    private PIDController chassisAngleController;
    
    private ScheduledExecutorService scheduler;
    public WheelManager wheelManager;

    private double lastHeadingTimestamp;  // Addresses counter spinning/snapback
    double lastGyro;

    public SwerveDriveSubsystem()
    {        
        super(true);  // set it into field relative by default
        
        configSwerve = new SwerveConfiguration();
        chassisAngleController = new PIDController(Constants.CHASSIS_P, Constants.CHASSIS_I, Constants.CHASSIS_D, 1.0);
        
        scheduler = Executors.newScheduledThreadPool(1);
        wheelManager = new WheelManager();
        
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
     * @param Velocity Direction the robot should move
     * @param Rotation How fast to spin while moving (range: -1 to 1)
     * @param Heading Auto-goto this angle (range: 0-360, -1 to disable)
     * @return actual wheel readings
     */
    public Vector[] driveWithHeading(Vector Velocity, double Rotation, double Heading)
    {
        ChassisVector command = new ChassisVector(Velocity.clone(), Rotation, Heading);

        ApplySpinMode(command);      // Pass by reference
        applyVelocityMode(command);  // Pass by reference
        printDash();
        
        return wheelManager.setWheelVectors(command.velocity, command.spin, configSwerve.gearHigh, m_brake);
    }
    
    private void ApplySpinMode(ChassisVector command)
    {
        double error = 0;
        String driveMode;

        chassisAngleController.setP(Preferences.getInstance().getDouble("SwervePID_P", Constants.CHASSIS_P));
        chassisAngleController.setI(Preferences.getInstance().getDouble("SwervePID_I", Constants.CHASSIS_I));
        chassisAngleController.setD(Preferences.getInstance().getDouble("SwervePID_D", Constants.CHASSIS_D));

        if (!command.ignoreHeading())
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

        SmartDashboard.putString("Drive Mode", driveMode);
        SmartDashboard.putNumber("Rotation Error", error);
    }
    
    protected void applyVelocityMode(ChassisVector command)
    {
        SmartDashboard.putNumber("Velocity X", command.velocity.getX());
        SmartDashboard.putNumber("Velocity Y", command.velocity.getY());
        SmartDashboard.putNumber("Rotation", command.spin);

        // If we're relative to the field, adjust the movement vector based on the gyro heading
        if (m_fieldRelative)
        {
            double AdjustedAngle = command.velocity.getAngle() + m_gyro.getYaw();
            
            AdjustedAngle = Utilities.wrapToRange(AdjustedAngle, -180, 180);                    
            command.velocity.setAngle(AdjustedAngle);
        }
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
        return wheelManager.getLastVector();
    }

    /**
     * Do a full wheel calibration, adjusting the angles by the specified values, and save the values for use
     * @param speed speed value to test against, 0-1
     * @param values array of values, -180 to 180, to adjust the wheel angle offsets
     */
    public void fullWheelCalibration(double speed, double values[], boolean save)
    {
        wheelManager.doFullWheelCalibration(speed, values, save);
    }

    public double[] getWheelCalibrations()
    {
        return wheelManager.getWheelCalibrations();
    }
    
    @Override
    public void printDash()
    {
        super.printDash();
        wheelManager.printDash();

        SmartDashboard.putBoolean("Gyro Enabled", configSwerve.gyroEnable);
        SmartDashboard.putBoolean("High Gear", configSwerve.gearHigh);
    }
}
