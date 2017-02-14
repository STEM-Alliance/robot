package org.wfrobotics.subsystems.drive.swerve;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class configures, updates, and commands the wheels
 */
public class WheelManager
{
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

    public class WheelConfiguration
    {
        private final boolean ENABLE_ACCELERATION_LIMIT = true;
        private final boolean ENABLE_CRAWL_MODE = true;
        private final boolean ENABLE_ROTATION_LIMIT = false;
        private final boolean ENABLE_VELOCITY_LIMIT = true;

        private double accelerationMax = 6; // Smaller is slower acceleration
        /**
         * Amount to scale back speeds (range: 0 (min - no crawl) to 1 (max - basically don't move))
         */
        public double crawlModeMagnitude = 0.0;
        private double rotationAdjustMin = .3;
        private double rotationAdjustRate = 1;
        private double velocityMaxAvailable = 1;
    }

    public WheelConfiguration config;
    private SwerveWheel[] wheels;

    private Vector lastVelocity;
    private double lastVelocityTimestamp;

    public WheelManager()
    {
        config = new WheelConfiguration();
        
        wheels = new SwerveWheel[SwerveConstants.WHEEL_COUNT];
        for (int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            wheels[i] = new SwerveWheel(i);
        }

        lastVelocity = new Vector(0, 0);
        lastVelocityTimestamp = Timer.getFPGATimestamp();
    }

    /**
     * Scale the wheel vectors based on max available velocity, adjust for rotation rate, then set/update the desired vectors individual wheels
     * @param RobotVelocity Robot's velocity using {@link Vector} type; max speed is 1.0
     * @param RobotRotation Robot's rotational movement; max rotation speed is -1 or 1
     * @param gear Which gear should the shifter use? True: High, False: Low
     * @param brake Enable brake mode? True: Yes, False: No
     * @return Array of {@link Vector} of the actual readings from the wheels
     */
    protected Vector[] setWheelVectors(Vector RobotVelocity, double RobotRotation, boolean gear, boolean brake)
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
        if(config.ENABLE_ROTATION_LIMIT)
        {
            config.rotationAdjustMin = Preferences.getInstance().getDouble("DRIVE_MIN_ROTATION", config.rotationAdjustMin);
            double RotationAdjust = Math.min(1 - RobotVelocity.getMag() + config.rotationAdjustMin, 1);
            RobotRotation = Utilities.clampToRange(RobotRotation, -RotationAdjust, RotationAdjust);
            RobotRotation *= config.rotationAdjustRate;
        }

        // Scale speed down to max of DRIVE_SPEED_CRAWL, then adjust range back up to 1
        if(config.ENABLE_CRAWL_MODE)
        {
            double crawlSpeed = Preferences.getInstance().getDouble("DRIVE_SPEED_CRAWL", SwerveConstants.DRIVE_SPEED_CRAWL);
            double scale = Utilities.scaleToRange(config.crawlModeMagnitude, 0, 1, crawlSpeed, 1);  // scale m_crawlMode from 0 and 1 to crawlSpeed and 1
            // Scale rotation and velocity back up
            RobotRotation *= scale;
            RobotVelocity.setMag(RobotVelocity.getMag() * scale);
        }

        // Limit the rate of change of velocity (ie limit acceleration)
        // Low limits will decrease the acceleration, slowing initial movements, smoothing out motion, but makes it less responsive.
        if(config.ENABLE_ACCELERATION_LIMIT)
        {
            RobotVelocity = restrictVelocity(RobotVelocity);
        }

        lastVelocity = RobotVelocity;

        SmartDashboard.putNumber("Drive X", RobotVelocity.getX());
        SmartDashboard.putNumber("Drive Y", RobotVelocity.getY());
        SmartDashboard.putNumber("Drive Mag", RobotVelocity.getMag());
        SmartDashboard.putNumber("Drive Ang", RobotVelocity.getAngle());
        SmartDashboard.putNumber("Drive R", RobotRotation);

        // Calculate vectors for each wheel
        for (int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            WheelsUnscaled[i] = new Vector(RobotVelocity.getX()
                    - RobotRotation
                    * wheels[i].position.getY(),
                    RobotVelocity.getY()
                    + RobotRotation
                    * wheels[i].position.getX());

            if (WheelsUnscaled[i].getMag() >= MaxWantedVeloc)
            {
                MaxWantedVeloc = WheelsUnscaled[i].getMag();
            }
        }

        double VelocityRatio = 1;

        if(config.ENABLE_VELOCITY_LIMIT)
        {
            // Grab max velocity from the dash
            config.velocityMaxAvailable = Preferences.getInstance().getDouble("MAX_ROBOT_VELOCITY", config.velocityMaxAvailable);

            // Determine ratio to scale all wheel velocities by
            VelocityRatio = config.velocityMaxAvailable / MaxWantedVeloc;

            VelocityRatio = (VelocityRatio > 1) ? 1:VelocityRatio;
        }

        for (int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            // Scale values for each wheel
            Vector WheelScaled = Vector.NewFromMagAngle(WheelsUnscaled[i].getMag() * VelocityRatio, WheelsUnscaled[i].getAngle());

            // Set the wheel speed
            WheelsActual[i] = wheels[i].setDesired(WheelScaled, gear, brake);
        }

        printDash();

        return WheelsActual;
    }

    /**
     * Scale speed down to max of DRIVE_SPEED_CRAWL, then adjust range back up to 1
     * @param vector
     * @return
     */
    public WheelVector applyCrawlMode(WheelVector vector)
    {
        double crawlSpeed = Preferences.getInstance().getDouble("DRIVE_SPEED_CRAWL", SwerveConstants.DRIVE_SPEED_CRAWL);
        double scale = Utilities.scaleToRange(config.crawlModeMagnitude, 0, 1, crawlSpeed, 1);  // scale m_crawlMode from 0 and 1 to crawlSpeed and 1

        // Scale rotation and velocity back up
        vector.spin *= scale;
        vector.velocity.setMag(vector.velocity.getMag() * scale);

        return vector;
    }

    /**
     * Returns the velocity restricted by the maximum acceleration
     * A low MAX_ACCELERATION value will slow the speed down  more than a high value
     * TODO: this should be replaced by a PID controller, probably... 
     * @param robotVelocity
     * @return
     */
    protected Vector restrictVelocity(Vector robotVelocity)
    {
        double TimeDelta = Timer.getFPGATimestamp() - lastVelocityTimestamp;
        lastVelocityTimestamp = Timer.getFPGATimestamp();

        // Get the difference between last velocity and this velocity
        Vector delta = robotVelocity.subtract(lastVelocity);

        // Grab the max acceleration value from the dash
        config.accelerationMax = Preferences.getInstance().getDouble("MAX_ACCELERATION", config.accelerationMax);

        // Determine if we are accelerating/decelerating too slow
        if (Math.abs(delta.getMag()) > config.accelerationMax * TimeDelta)
        {
            // if we are, slow that down by the MaxAcceleration value
            delta.setMag(config.accelerationMax * TimeDelta);
            robotVelocity = lastVelocity.add(delta);
        }

        return robotVelocity;
    }

    /**
     * Get the last movement vector of the robot, relative to the robot heading.
     * the adjustment for field relative mode, if applicable, has already been taken
     * into consideration
     * @return movement vector relative to the robot heading
     */
    public Vector getLastVector()
    {
        return lastVelocity;
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
            wheels[i].updateAngleOffset(values[i]);
            wheels[i].setDesired(vector, false, false);

            if(save)
            {
                wheels[i].saveAngleOffset(values[i]);
            }
        }
    }

    public double[] getWheelCalibrations()
    {
        double[] cals = {0,0,0,0};
        for(int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            cals[i] = wheels[i].getAngleOffset();
        }
        return cals;
    }

    public void printDash()
    {
        for(int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            wheels[i].printDash();
        }
    }

    /**
     * For unit testing
     */
    public void free()
    {
        for (int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            wheels[i].free();
        }
    }   
}
