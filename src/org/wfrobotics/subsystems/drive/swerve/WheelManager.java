package org.wfrobotics.subsystems.drive.swerve;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class configures, updates, and commands the wheels
 */
public class WheelManager implements Runnable
{
    public static class RobotCommand
    {
        public Vector velocity;
        public double spin;

        public RobotCommand(Vector velocity,  double rotationalSpeed)
        {
            this.velocity = velocity;
            this.spin = rotationalSpeed;
        }
    }

    public class WheelConfiguration
    {
        private final boolean ENABLE_ACCELERATION_LIMIT = true;
        private final boolean ENABLE_CRAWL_MODE = true;
        private final boolean CRAWL_MODE_DEFAULT_HIGH = true;
        private final boolean ENABLE_SQUARE_MAGNITUDE = true;
        private final boolean ENABLE_ROTATION_LIMIT = true;

        private double accelerationMax = 6; // Smaller is slower acceleration
        
        /**
         * Amount to scale back speeds (range: 0 (min - no crawl) to 1 (max - basically don't move))
         * This should get set from the controller/command
         */
        public double crawlModeMagnitude = 0.0;
        
        private double rotationAdjustMin = .3;
        private double velocityMaxAvailable = 1;
    }

    public WheelConfiguration config;
    private SwerveWheel[] wheels;  // TODO test this as static for performance

    private Vector lastVelocity;
    private double lastVelocityTimestamp;
    
    Vector requestedRobotVelocity; 
    double requestedRobotRotation; 
    boolean requestedGear;
    boolean requestedBrake;

    public WheelManager()
    {
        config = new WheelConfiguration();
        
        wheels = new SwerveWheel[Constants.WHEEL_COUNT];
        for (int i = 0; i < Constants.WHEEL_COUNT; i++)
        {
            wheels[i] = new SwerveWheel(i, new Vector(Constants.WHEEL_POSITIONS[i]));
        }

        lastVelocity = new Vector(0, 0);
        lastVelocityTimestamp = Timer.getFPGATimestamp();
    }
    
    @Override  // Entry point for running this class as a runnable/thread
    public void run()
    {
        setWheelVectors(requestedRobotVelocity, requestedRobotRotation, requestedGear, requestedBrake);
    }
    
    public synchronized void updateWheelVectors(Vector RobotVelocity, double RobotRotation, boolean gear, boolean brake)
    {
        requestedRobotVelocity = RobotVelocity; 
        requestedRobotRotation = RobotRotation; 
        requestedGear = gear;
        requestedBrake = brake;
    }

    /**
     * Scale the wheel vectors based on max available velocity, adjust for rotation rate, then set/update the desired vectors individual wheels
     * @param RobotVelocity Robot's velocity using {@link Vector} type; max speed is 1.0
     * @param RobotRotation Robot's rotational movement; max rotation speed is -1 or 1
     * @param gear Which gear should the shifter use? True: High, False: Low
     * @param brake Enable brake mode? True: Yes, False: No
     * @return Array of {@link Vector} of the actual readings from the wheels
     */
    Vector[] setWheelVectors(Vector RobotVelocity, double RobotRotation, boolean gear, boolean brake)
    {
        RobotCommand robot = new RobotCommand(RobotVelocity, RobotRotation);
        Vector[] WheelsScaled;
        Vector[] WheelsActual = new Vector[Constants.WHEEL_COUNT];

        robot = applyClampVelocity(robot);
        robot = (config.ENABLE_SQUARE_MAGNITUDE) ? applyMagnitudeSquare(robot):robot;
        robot = (config.ENABLE_ROTATION_LIMIT) ? applyRotationLimit(robot):robot;  
        robot = (config.ENABLE_CRAWL_MODE) ? applyCrawlMode(robot):robot;
        robot = (config.ENABLE_ACCELERATION_LIMIT) ? applyAccelerationLimit(robot):robot;
        lastVelocity = robot.velocity;

        SmartDashboard.putNumber("Drive X", robot.velocity.getX());
        SmartDashboard.putNumber("Drive Y", robot.velocity.getY());
        SmartDashboard.putNumber("Drive Mag", robot.velocity.getMag());
        SmartDashboard.putNumber("Drive Ang", robot.velocity.getAngle());
        SmartDashboard.putNumber("Drive R", robot.spin);

        WheelsScaled = scaleWheelVectors(robot);
        
        for (int i = 0; i < Constants.WHEEL_COUNT; i++)
        {
            WheelsActual[i] = wheels[i].setDesired(WheelsScaled[i], gear, brake);
        }

        printDash();

        return WheelsActual;
    }
    
    public double getVelocityLimit(double MaxWantedVeloc)
    {
        config.velocityMaxAvailable = Preferences.getInstance().getDouble("MAX_ROBOT_VELOCITY", config.velocityMaxAvailable);
        double velocityRatio = 1;

        // Determine ratio to scale all wheel velocities by
        velocityRatio = config.velocityMaxAvailable / MaxWantedVeloc;

        velocityRatio = (velocityRatio > 1) ? 1:velocityRatio;

        return velocityRatio;
    }

    /**
     * Get the last movement vector of the robot, relative to the robot heading.
     * the adjustment for field relative mode, if applicable, has already been taken into consideration
     * @return movement vector relative to the robot heading
     */
    public Vector getLastVector()
    {
        return lastVelocity;
    }

    /**
     * Do a full wheel calibration, adjusting the angles by the specified values, and save the values for use
     * @param speed speed value to test against, 0-1
     * @param values array of values, -180 to 180, to adjust the wheel angle offsets
     */
    public synchronized void doFullWheelCalibration(double speed, double values[], boolean save)
    {

        Vector vector = Vector.NewFromMagAngle(speed, 0);

        for(int i = 0; i < Constants.WHEEL_COUNT; i++)
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
        double[] cals = new double[Constants.WHEEL_COUNT];
        for(int i = 0; i < Constants.WHEEL_COUNT; i++)
        {
            cals[i] = wheels[i].getAngleOffset();
        }
        return cals;
    }

    public void printDash()
    {
        for(int i = 0; i < Constants.WHEEL_COUNT; i++)
        {
            wheels[i].printDash();
        }
    }

    /**
     * Scale each wheel vector set to within range. Values are scaled down relative to the fastest wheel.
     * @param robot
     * @return Scaled vectors to command the wheels with
     */
    private Vector[] scaleWheelVectors(RobotCommand robot)
    {
        Vector[] WheelsUnscaled = new Vector[Constants.WHEEL_COUNT];
        Vector[] WheelsScaled = new Vector[Constants.WHEEL_COUNT];
        double MaxWantedVeloc = 0;
        double VelocityRatio;

        for (int i = 0; i < Constants.WHEEL_COUNT; i++)
        {
            WheelsUnscaled[i] = new Vector(robot.velocity.getX() - robot.spin * wheels[i].POSITION_RELATIVE_TO_CENTER.getY(),
                    -(robot.velocity.getY() + robot.spin * wheels[i].POSITION_RELATIVE_TO_CENTER.getX()));

            if (WheelsUnscaled[i].getMag() >= MaxWantedVeloc)
            {
                MaxWantedVeloc = WheelsUnscaled[i].getMag();
            }
        }

        VelocityRatio = getVelocityLimit(MaxWantedVeloc);

        for (int i = 0; i < Constants.WHEEL_COUNT; i++)
        {
            // Scale values for each wheel
            WheelsScaled[i] = Vector.NewFromMagAngle(WheelsUnscaled[i].getMag() * VelocityRatio, WheelsUnscaled[i].getAngle());
        }

        return WheelsScaled;
    }

    /**
     * Set limitations on speed
     * @param robot
     * @return
     */
    private RobotCommand applyClampVelocity(RobotCommand robot)
    {
        double RobotVelocityClamped = (robot.velocity.getMag() > 1.0) ? 1:robot.velocity.getMag();

        robot.velocity.setMag(RobotVelocityClamped);

        return robot;
    }

    /**
     * By squaring the magnitude, we get more fine adjustments at low speed but keep the sign since negative squared is positive
     * @param robot
     * @return
     */
    private RobotCommand applyMagnitudeSquare(RobotCommand robot)
    {
        robot.velocity.setMag(Math.signum(robot.velocity.getMag()) * Math.pow(robot.velocity.getMag(), 2));
        return robot;
    }

    /**
     * Limit before slowing speed so it runs using the original values set limitations on rotation, so if driving full speed it doesn't take priority
     * @param robot
     * @return
     */
    private RobotCommand applyRotationLimit(RobotCommand robot)
    {
        config.rotationAdjustMin = Preferences.getInstance().getDouble("DRIVE_MIN_ROTATION", config.rotationAdjustMin);
        double RotationAdjust = Math.min(1 - robot.velocity.getMag() + config.rotationAdjustMin, 1);

        //robot.spin = Utilities.clampToRange(robot.spin, -RotationAdjust, RotationAdjust);
        robot.spin *= RotationAdjust;
        SmartDashboard.putNumber("SwerveRotationAdjust", RotationAdjust);

        return robot;
    }

    /**
     * Scale speed down to max of DRIVE_SPEED_CRAWL, then adjust range back up to 1
     * @param robot
     * @return
     */
    private RobotCommand applyCrawlMode(RobotCommand robot)
    {
        double crawlSpeed = Preferences.getInstance().getDouble("DRIVE_SPEED_CRAWL", Constants.DRIVE_SPEED_CRAWL);
        double scale = 1;
        
        if(config.CRAWL_MODE_DEFAULT_HIGH)
        {
            scale = Utilities.scaleToRange(1 - config.crawlModeMagnitude, 0, 1, crawlSpeed, 1);  // scale m_crawlMode from 0 and 1 to crawlSpeed and 1
        }
        else
        {
            scale = Utilities.scaleToRange(config.crawlModeMagnitude, 0, 1, crawlSpeed, 1);  // scale m_crawlMode from 0 and 1 to crawlSpeed and 1
        }
        
        // Scale rotation and velocity back up
        robot.spin *= scale;
        robot.velocity.setMag(robot.velocity.getMag() * scale);

        return robot;
    }

    /**
     * Returns the velocity restricted by the maximum acceleration
     * A low MAX_ACCELERATION value will slow the speed down  more than a high value
     * TODO: this should be replaced by a PID controller, probably... 
     * @param robot
     * @return
     */
    private RobotCommand applyAccelerationLimit(RobotCommand robot)
    {
        double TimeDelta = Timer.getFPGATimestamp() - lastVelocityTimestamp;
        lastVelocityTimestamp = Timer.getFPGATimestamp();

        // Get the difference between last velocity and this velocity
        Vector delta = robot.velocity.subtract(lastVelocity);

        // Grab the max acceleration value from the dash
        config.accelerationMax = Preferences.getInstance().getDouble("MAX_ACCELERATION", config.accelerationMax);

        // Determine if we are accelerating/decelerating too slow
        if (Math.abs(delta.getMag()) > config.accelerationMax * TimeDelta)
        {
            // if we are, slow that down by the MaxAcceleration value
            delta.setMag(config.accelerationMax * TimeDelta);
            robot.velocity = lastVelocity.add(delta);
        }

        return robot;
    }
}
