package org.wfrobotics.subsystems.drive.swerve;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.commands.drive.*;
import org.wfrobotics.subsystems.drive.DriveSubsystem;

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
        Vector velocity;
        double spin;
        double heading;

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
    }
    
    public class WheelVector
    {
        Vector velocity;
        double spin;
        
        public WheelVector(Vector velocity,  double rotationalSpeed)
        {
            this.velocity = velocity;
            this.spin = rotationalSpeed;
        }
    }
    
    public class SwerveConfiguration
    {
        /**
         * Sets shifter to high or low gear (True: High gear, False: Low gear)
         */
        public boolean m_gearHigh = false;
        public boolean m_gyroEnabled = true;
        private final double HEADING_TIMEOUT = .2;
        private final double AUTO_ROTATION_MIN = .1;  // this will hopefully prevent locking the wheels when slowing down
    }
    
    public class WheelConfiguration
    {
        private final boolean ENABLE_CRAWL_MODE = true;
        /**
         * Amount to scale back speeds (range: 0 (min - no crawl) to 1 (max - basically don't move))
         */
        public double crawlModeMagnitude = 0.0;
        private final boolean ENABLE_ACCELERATION_LIMIT = true;
        private double m_maxAcceleration = 6; // Smaller is slower acceleration
        private final boolean ENABLE_VELOCITY_LIMIT = true;
        private double m_maxAvailableVelocity = 1;
        private final boolean ENABLE_ROTATION_LIMIT = false;
        private double m_minRotationAdjust = .3;
        private double m_rotationRateAdjust = 1;
    }

    public static final double HEADING_IGNORE = -1;

    public SwerveConfiguration configSwerve;
    public WheelConfiguration configWheel;
    protected SwerveWheel[] m_wheels;    
    private PIDController m_chassisAngleController;

    private Vector m_lastVelocity;
    private double m_lastVelocityTimestamp;
    private double m_lastHeadingTimestamp;  // this is used to address the counter spinning/snapback

    double m_gyroLast;

    /**
     * sets up individual wheels and their positions relative to robot center
     */
    public SwerveDriveSubsystem()
    {        
        super(true);  // set it into field relative by default
        
        configSwerve = new SwerveConfiguration();
        m_chassisAngleController = new PIDController(.035, .00001, 0, 1.0);
        m_wheels = new SwerveWheel[SwerveConstants.WHEEL_COUNT];
        for (int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            m_wheels[i] = new SwerveWheel(i);
        }
        
        m_lastVelocity = new Vector(0, 0);
    }

    public void initDefaultCommand() 
    {
        setDefaultCommand(new DriveSwerve(DriveSwerve.MODE.HALO));
    }

    @Override
    public void driveTank(double right, double left)
    {
        //TODO?
    }

    @Override
    public void driveVector(double magnitude, double angle, double rotation)
    {
        driveVector(Vector.NewFromMagAngle(magnitude, angle), rotation);
    }

    @Override
    public void driveVector(Vector velocity, double rotation)
    {
        driveWithHeading(velocity, rotation, HEADING_IGNORE);
    }

    @Override
    public void driveXY(double x, double y, double rotation)
    {
        driveWithHeading(new Vector(x,y), rotation, HEADING_IGNORE);
    }

    /**
     * Main drive update function, allows for xy movement, yaw rotation, and turn to angle/heading
     * @param Velocity {@link Vector} of xy movement of robot
     * @param Rotation robot's rotational movement, -1 to 1 rad/s
     * @param Heading 0-360 of angle to turn to, -1 if not in use
     * @return actual wheel readings
     */
    public Vector[] driveWithHeading(Vector Velocity, double Rotation, double Heading)
    {
        ChassisVector cv = new ChassisVector(Velocity, Rotation, Heading);

        cv.spin = ApplySpinMode(cv);
        cv.velocity = applyVelocityMode(cv);
        
        return setWheelVectors(Velocity, Rotation);
    }
    
    private double ApplySpinMode(ChassisVector cv)
    {
        double Error = 0;

        // Determine which drive mode to use between
        if (!cv.ignoreHeading())
        {
            // rotate to angle
            SmartDashboard.putString("Drive Mode", "Rotate To Heading");

            // this should snap us to a specific angle
            if(configSwerve.m_gyroEnabled)
            {
                // set the rotation using a PID controller based on current robot
                // heading and new desired heading
                Error = Utilities.wrapToRange(cv.heading - m_gyro.getYaw(), -180, 180);
                cv.spin = m_chassisAngleController.update(Error);
            }
            m_lastHeading = cv.heading;
        }
        else 
        {
            if (Math.abs(cv.spin) > .1)
            {
                // spinning
                SmartDashboard.putString("Drive Mode", "Spinning");

                // just take the rotation value from the controller
                m_lastHeading = m_gyro.getYaw();

                // save off timestamp to counter snapback
                m_lastHeadingTimestamp = Timer.getFPGATimestamp();

                m_gyroLast = m_gyro.getYaw();

                // square rotation value to give it more control at lower values
                // but keep the same sign since a negative squared is positive
                cv.spin = Math.signum(cv.spin) * Math.pow(cv.spin, 2);
            }
            else // maintain angle
            {
                // Delay the stay at angle to prevent snapback
                if((Timer.getFPGATimestamp() - m_lastHeadingTimestamp) > configSwerve.HEADING_TIMEOUT)
                {
                    SmartDashboard.putString("Drive Mode", "Stay At Angle");

                    if(configSwerve.m_gyroEnabled)
                    {
                        // this should keep us facing the same direction

                        // set the rotation using a PID controller based on current robot
                        // heading and new desired heading
                        Error = -Utilities.wrapToRange(m_lastHeading - m_gyro.getYaw(), -180, 180);
                        double tempRotation = m_chassisAngleController.update(Error);

                        // add a deadband to hopefully help with wheel lock after stopping
                        SmartDashboard.putNumber("MaintainRotation", tempRotation);
                        if(tempRotation > configSwerve.AUTO_ROTATION_MIN)
                        {
                            cv.spin = tempRotation;
                        }
                    }
                }
                else
                {
                    m_lastHeading = m_gyro.getYaw(); // save off the latest heading until the timeout
                }
            }
        }

        SmartDashboard.putNumber("Rotation Error", Error);
        
        return cv.spin;
    }
    
    protected Vector applyVelocityMode(ChassisVector cv)
    {
        SmartDashboard.putNumber("Velocity X", cv.velocity.getX());
        SmartDashboard.putNumber("Velocity Y", cv.velocity.getY());
        SmartDashboard.putNumber("Rotation", cv.spin);

        // If we're relative to the field, we need to adjust the movement vector based on the gyro heading
        if (m_fieldRelative)
        {
            double AdjustedAngle = cv.velocity.getAngle() + m_gyro.getYaw();
            
            AdjustedAngle = Utilities.wrapToRange(AdjustedAngle, -180, 180);
            
            SmartDashboard.putNumber("FieldRelativePre", cv.velocity.getAngle());            
            cv.velocity.setAngle(AdjustedAngle);
            SmartDashboard.putNumber("FieldRelativePost", cv.velocity.getAngle());
        }

        return cv.velocity;
    }

    /**
     * Scale the wheel vectors based on max available velocity, adjust for
     * rotation rate, then set/update the desired vectors individual wheels
     * @param RobotVelocity robot's velocity using {@link Vector} type; max speed is 1.0
     * @param RobotRotation robot's rotational movement; max rotation speed is -1 or 1
     * @return Array of {@link Vector} of the actual readings from the wheels
     */
    protected Vector[] setWheelVectors(Vector RobotVelocity, double RobotRotation)
    {
        Vector[] WheelsUnscaled = new Vector[SwerveConstants.WHEEL_COUNT];
        Vector[] WheelsActual = new Vector[SwerveConstants.WHEEL_COUNT];
        double MaxWantedVeloc = 0;
        double RobotVelocityClamped = (RobotVelocity.getMag() > 1.0) ? 1:RobotVelocity.getMag();
        
        RobotVelocity.setMag(RobotVelocityClamped);  // Set limitations on speed

        // By squaring the magnitude, we get more fine adjustments at low speed but keep the sign since negative squared is positive
        RobotVelocity.setMag(Math.signum(RobotVelocity.getMag()) * Math.pow(RobotVelocity.getMag(), 2));

        // limit before slowing speed so it runs using the original values
        // set limitations on rotation, so if driving full speed it doesn't take priority
        if(configWheel.ENABLE_ROTATION_LIMIT)
        {
            configWheel.m_minRotationAdjust = Preferences.getInstance().getDouble("DRIVE_MIN_ROTATION", configWheel.m_minRotationAdjust);
            double RotationAdjust = Math.min(1 - RobotVelocity.getMag() + configWheel.m_minRotationAdjust, 1);
            RobotRotation = Utilities.clampToRange(RobotRotation, -RotationAdjust, RotationAdjust);
            RobotRotation *= configWheel.m_rotationRateAdjust;
        }
        
        // Scale speed down to max of DRIVE_SPEED_CRAWL, then adjust range back up to 1
        if(configWheel.ENABLE_CRAWL_MODE)
        {
            double crawlSpeed = Preferences.getInstance().getDouble("DRIVE_SPEED_CRAWL", SwerveConstants.DRIVE_SPEED_CRAWL);
            double scale = Utilities.scaleToRange(configWheel.crawlModeMagnitude, 0, 1, crawlSpeed, 1);  // scale m_crawlMode from 0 and 1 to crawlSpeed and 1

            // Scale rotation and velocity back up
            RobotRotation *= scale;
            RobotVelocity.setMag(RobotVelocity.getMag() * scale);
        }

        // Limit the rate of change of velocity (ie limit acceleration)
        // Low limits will decrease the acceleration, slowing initial movements, smoothing out motion, but makes it less responsive.
        if(configWheel.ENABLE_ACCELERATION_LIMIT)
        {
            RobotVelocity = restrictVelocity(RobotVelocity);
        }

        m_lastVelocity = RobotVelocity;

        SmartDashboard.putNumber("Drive X", RobotVelocity.getX());
        SmartDashboard.putNumber("Drive Y", RobotVelocity.getY());
        SmartDashboard.putNumber("Drive Mag", RobotVelocity.getMag());
        SmartDashboard.putNumber("Drive Ang", RobotVelocity.getAngle());
        SmartDashboard.putNumber("Drive R", RobotRotation);

        // calculate vectors for each wheel
        for (int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            // calculate
            WheelsUnscaled[i] = new Vector(RobotVelocity.getX()
                    - RobotRotation
                    * m_wheels[i].position.getY(),
                    RobotVelocity.getY()
                    + RobotRotation
                    * m_wheels[i].position.getX());

            if (WheelsUnscaled[i].getMag() >= MaxWantedVeloc)
            {
                MaxWantedVeloc = WheelsUnscaled[i].getMag();
            }
        }

        double VelocityRatio = 1;

        if(configWheel.ENABLE_VELOCITY_LIMIT)
        {
            // grab max velocity from the dash
            configWheel.m_maxAvailableVelocity = Preferences.getInstance().getDouble("MAX_ROBOT_VELOCITY", configWheel.m_maxAvailableVelocity);

            // determine ratio to scale all wheel velocities by
            VelocityRatio = configWheel.m_maxAvailableVelocity / MaxWantedVeloc;

            if (VelocityRatio > 1)
            {
                VelocityRatio = 1;
            }
        }

        boolean actualGearHigh = configSwerve.m_gearHigh ? SwerveConstants.SHIFTER_DEFAULT_HIGH : !SwerveConstants.SHIFTER_DEFAULT_HIGH;

        for (int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            // Scale values for each wheel
            Vector WheelScaled = Vector.NewFromMagAngle(WheelsUnscaled[i].getMag() * VelocityRatio, WheelsUnscaled[i].getAngle());

            // Set the wheel speed
            WheelsActual[i] = m_wheels[i].setDesired(WheelScaled, actualGearHigh, m_brake);
        }

        printDash();

        return WheelsActual;
    }

    /**
     * Returns the velocity restricted by the maximum acceleration
     * A low MAX_ACCELERATION value will slow the speed down  more than a high value
     * TODO: this should be replaced by a PID controller, probably...
     * 
     * @param robotVelocity
     * @return
     */
    protected Vector restrictVelocity(Vector robotVelocity)
    {
        double TimeDelta = Timer.getFPGATimestamp() - m_lastVelocityTimestamp;
        m_lastVelocityTimestamp = Timer.getFPGATimestamp();

        // get the difference between last velocity and this velocity
        Vector delta = robotVelocity.subtract(m_lastVelocity);

        // grab the max acceleration value from the dash
        configWheel.m_maxAcceleration = Preferences.getInstance().getDouble("MAX_ACCELERATION", configWheel.m_maxAcceleration);

        // determine if we are accelerating/decelerating too slow
        if (Math.abs(delta.getMag()) > configWheel.m_maxAcceleration * TimeDelta)
        {
            // if we are, slow that down by the MaxAcceleration value
            delta.setMag(configWheel.m_maxAcceleration * TimeDelta);
            robotVelocity = m_lastVelocity.add(delta);
        }

        return robotVelocity;
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
     * the adjustment for field relative mode, if applicable, has already been taken
     * into consideration
     * @return movement vector relative to the robot heading
     */
    public Vector getLastVector()
    {
        return m_lastVelocity;
    }

    /**
     * Get the actual reading of a wheel
     * @param index Index of the wheel
     * @return Actual reading of the wheel
     */
    public Vector getWheelActual(int index)
    {
        return m_wheels[index].getActual();
    }

    /**
     * Do a full wheel calibration, adjusting the angles by the specified values,
     * and save the values for use
     * @param speed speed value to test against, 0-1
     * @param values array of values, -180 to 180, to adjust the wheel angle offsets
     */
    public void fullWheelCalibration(double speed, double values[], boolean save)
    {

        Vector vector = Vector.NewFromMagAngle(speed, 0);

        for(int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            m_wheels[i].updateAngleOffset(values[i]);
            m_wheels[i].setDesired(vector, false, false);

            if(save)
            {
                m_wheels[i].saveAngleOffset(values[i]);
            }
        }
    }

    public double[] getWheelCalibrations()
    {
        double[] cals = {0,0,0,0};
        for(int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            cals[i] = m_wheels[i].getAngleOffset();
        }
        return cals;
    }
    
    @Override
    public void printDash()
    {
        super.printDash();

        for(int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            m_wheels[i].printDash();
        }

        SmartDashboard.putBoolean("Gyro Enabled", configSwerve.m_gyroEnabled);
        SmartDashboard.putBoolean("High Gear", configSwerve.m_gearHigh);
    }

    /**
     * For unit testing
     */
    public void free()
    {
        for (int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            m_wheels[i].free();
        }
    }    
}
