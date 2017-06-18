package org.wfrobotics.reuse.subsystems.swerve.chassis;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.reuse.subsystems.swerve.wheel.SwerveWheel;
import org.wfrobotics.reuse.utilities.HerdVector;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class controls swerve drive at the robot/chassis level
 * @author Team 4818 WFRobotics
 */
public class SwerveChassis implements Runnable
{
    public static class RobotCommand
    {
        public HerdVector velocity;
        public double spin;

        public RobotCommand(HerdVector velocity,  double rotationalSpeed)
        {
            this.velocity = velocity;
            this.spin = rotationalSpeed;
        }
    }

    public class Config
    {
        private final boolean ENABLE_ACCELERATION_LIMIT = true;
        private final boolean CRAWL_MODE_ENABLE = true;
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

    public Config config;
    private SwerveWheel[] wheels;  // TODO test this as static for performance

    private HerdVector lastVelocity;
    private double lastVelocityTimestamp;
    private double debugLastUpdate;  // Measure thread loop period
    
    HerdVector requestedRobotVelocity;
    double requestedRobotRotation;
    boolean requestedGear;
    boolean requestedBrake;

    public SwerveChassis()
    {
        config = new Config();
        
        wheels = new SwerveWheel[Constants.WHEEL_COUNT];
        for (int i = 0; i < Constants.WHEEL_COUNT; i++)
        {
            wheels[i] = new SwerveWheel(Constants.WHEEL_IDS[i], i, new Vector(Constants.WHEEL_POSITIONS[i]));
        }

        lastVelocity = new HerdVector(0, 0);
        lastVelocityTimestamp = Timer.getFPGATimestamp();
        debugLastUpdate = Timer.getFPGATimestamp();
    }
    
    @Override  // Entry point for running this class as a runnable/thread
    public void run()
    {
        setWheelVectors(requestedRobotVelocity, requestedRobotRotation, requestedGear, requestedBrake);
        printDash();

        SmartDashboard.putNumber("SwerveUpdateRate", Timer.getFPGATimestamp() - debugLastUpdate);
        debugLastUpdate = Timer.getFPGATimestamp();
    }
    
    public synchronized void updateWheelVectors(HerdVector robotVelocity, double robotRotation, boolean gear, boolean brake)
    {
        requestedRobotVelocity = robotVelocity; 
        requestedRobotRotation = robotRotation; 
        requestedGear = gear;
        requestedBrake = brake;
    }

    /**
     * Scale the wheel vectors based on max available velocity, adjust for rotation rate, then set/update the desired vectors individual wheels
     * @param robotVelocity Robot's velocity using {@link Vector} type; max speed is 1.0
     * @param robotRotation Robot's rotational movement; max rotation speed is -1 or 1
     * @param gear Which gear should the shifter use? True: High, False: Low
     * @param brake Enable brake mode? True: Yes, False: No
     * @return Array of {@link Vector} of the actual readings from the wheels
     */
    public Vector[] setWheelVectors(HerdVector robotVelocity, double robotRotation, boolean gear, boolean brake)
    {
        RobotCommand robot = new RobotCommand(robotVelocity, robotRotation);
        Vector[] WheelsScaled;
        Vector[] WheelsActual = new Vector[Constants.WHEEL_COUNT];

        robot = applyClampVelocity(robot);
        robot = (config.ENABLE_SQUARE_MAGNITUDE) ? applyMagnitudeSquare(robot) : robot;
        robot = (config.ENABLE_ROTATION_LIMIT) ? applyRotationLimit(robot) : robot;  
        robot = (config.CRAWL_MODE_ENABLE) ? applyCrawlMode(robot) : robot;
        robot = (config.ENABLE_ACCELERATION_LIMIT) ? applyAccelerationLimit(robot) : robot;
        lastVelocity = robot.velocity;

        SmartDashboard.putNumber("Drive X", robot.velocity.getX());
        SmartDashboard.putNumber("Drive Y", robot.velocity.getY());
        SmartDashboard.putNumber("Drive Mag", robot.velocity.getMag());
        SmartDashboard.putNumber("Drive Ang", robot.velocity.getAngle());
        SmartDashboard.putNumber("Drive R", robot.spin);

        WheelsScaled = scaleWheelVectors(robot);
        
        for (int i = 0; i < Constants.WHEEL_COUNT; i++)
        {
            WheelsActual[i] = wheels[i].set(WheelsScaled[i], gear, brake);
        }

        return WheelsActual;
    }
    
    private double getVelocityLimit(double MaxWantedVelocity)
    {
        config.velocityMaxAvailable = Preferences.getInstance().getDouble("MAX_ROBOT_VELOCITY", config.velocityMaxAvailable);
        double velocityRatio = 1;

        // Determine ratio to scale all wheel velocities by
        velocityRatio = config.velocityMaxAvailable / MaxWantedVelocity;

        velocityRatio = (velocityRatio > 1) ? 1:velocityRatio;

        return velocityRatio;
    }

    /**
     * Last field relative vector for the whole robot
     * @return 
     */
    public HerdVector getLastVector()
    {
        return lastVelocity;
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
            WheelsScaled[i] = Vector.NewFromMagAngle(WheelsUnscaled[i].getMag() * VelocityRatio, WheelsUnscaled[i].getAngle());
        }

        return WheelsScaled;
    }

    private RobotCommand applyClampVelocity(RobotCommand robot)
    {
        double RobotVelocityClamped = (robot.velocity.getMag() > 1.0) ? 1 : robot.velocity.getMag();
        HerdVector clamped = new HerdVector(RobotVelocityClamped, robot.velocity.getAngle());

        return new RobotCommand(clamped, robot.spin);
    }

    private RobotCommand applyMagnitudeSquare(RobotCommand robot)  // Finer control at low speed
    {
        robot.velocity.scale(robot.velocity);

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

    private RobotCommand applyCrawlMode(RobotCommand robot)  // Scale speed down to max of DRIVE_SPEED_CRAWL, then adjust range back up to 1
    {
        double crawlSpeed = Preferences.getInstance().getDouble("DRIVE_SPEED_CRAWL", Constants.DRIVE_SPEED_CRAWL);
        double crawlMag = (config.CRAWL_MODE_DEFAULT_HIGH) ? 1 - config.crawlModeMagnitude : config.crawlModeMagnitude;
        double scalingFactor = Utilities.scaleToRange(crawlMag, 0, 1, crawlSpeed, 1);  // scale m_crawlMode from 0 and 1 to crawlSpeed and 1
        
        robot.spin *= scalingFactor;
        robot.velocity.scale(scalingFactor);

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
        double now = Timer.getFPGATimestamp();
        double dt = now - lastVelocityTimestamp;
        HerdVector delta = robot.velocity.sub(lastVelocity);
        
        lastVelocityTimestamp = now;
        config.accelerationMax = Preferences.getInstance().getDouble("MAX_ACCELERATION", config.accelerationMax);

        // Accelerating/decelerating too fast? Slow down
        if (Math.abs(delta.getMag()) > config.accelerationMax * dt)
        {
            HerdVector correctionFactor = new HerdVector(config.accelerationMax * dt, 0);
            robot.velocity = lastVelocity.add(correctionFactor);  // TODO We should set, not add?
        }

        return robot;
    }
}
