/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wfrobotics.subsystems.drive.swerve;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.commands.drive.*;
import org.wfrobotics.hardware.Gyro;
import org.wfrobotics.subsystems.drive.DriveSubsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Swerve chassis implementation
 * 
 * @author Team 4818 WFRobotics
 */
public class SwerveDriveSubsystem extends DriveSubsystem 
{
    public static final  double HEADING_IGNORE = -1;
    
    protected SwerveWheel[] m_wheels;
    
    private boolean m_gearHigh;

    private PIDController m_chassisAngleController;
    private double m_chassisP = .015;
    private double m_chassisI = 0.0;
    private double m_chassisD = 0;

    private double m_crawlMode = 0.0;
    private final boolean ENABLE_CRAWL_MODE = true;
    
    private double m_maxAcceleration = 6; // Smaller is slower acceleration
    private final boolean ENABLE_ACCELERATION_LIMIT = true;
    
    private double m_maxAvailableVelocity = 1;
    private final boolean ENABLE_VELOCITY_LIMIT = true;

    private double m_minRotationAdjust = .3;
    protected double m_rotationRateAdjust = 1;
    private final boolean ENABLE_ROTATION_LIMIT = false;

    private Vector m_lastVelocity;
    private double m_lastVelocityTimestamp;
    
    // this is used to address the counter spinning/snapback
    private double m_lastHeadingTimestamp;
    private final double HEADING_TIMEOUT = .2;
    
    // this will hopefully prevent locking the wheels when slowing down
    private final double AUTO_ROTATION_MIN = .1;
    
    double gyroLast;

    /**
     * sets up individual wheels and their positions relative to robot center
     */
    public SwerveDriveSubsystem()
    {
        super();
        
        m_chassisAngleController = new PIDController(m_chassisP, m_chassisI, m_chassisD, 1.0);

        m_lastVelocity = new Vector(0, 0);

        m_wheels = new SwerveWheel[SwerveConstants.WHEEL_COUNT];

        // creat the wheel objects
        for (int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            // use SRXs for the angle inputs
            m_wheels[i] = new SwerveWheel(i);
        }
    }
    
    /**
     * set the default command
     */
    public void initDefaultCommand() 
    {
        setDefaultCommand(new DriveSwerve(DriveSwerve.MODE.HALO));
    }
    
    public void free()
    {
        for (int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            m_wheels[i].free();
        }
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
    
    @Override
    public void printDash()
    {
        super.printDash();
        
        for(int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            m_wheels[i].printDash();
        }

        SmartDashboard.putBoolean("High Gear", m_gearHigh);
    }
    
    /**
     * Main drive update function, allows for xy movement, yaw rotation, and
     * turn to angle/heading
     * 
     * @param Velocity {@link Vector} of xy movement of robot
     * @param Rotation robot's rotational movement, -1 to 1 rad/s
     * @param Heading 0-360 of angle to turn to, -1 if not in use
     * @return actual wheel readings
     */
    public Vector[] driveWithHeading(Vector Velocity, double Rotation, double Heading)
    {
        double Error = 0;
        
        // determine which drive mode to use between
        if (Heading != HEADING_IGNORE)
        {
            // rotate to angle
            SmartDashboard.putString("Drive Mode", "Rotate To Heading");
            
            // this should snap us to a specific angle
            
            // set the rotation using a PID controller based on current robot
            // heading and new desired heading
            Error = Utilities.wrapToRange(Heading - m_gyro.getYaw(), -180, 180);
            Rotation = m_chassisAngleController.update(Error);
            m_lastHeading = Heading;
        }
        else if (Math.abs(Rotation) > .1)
        {
            // spinning
            SmartDashboard.putString("Drive Mode", "Spinning");
            
            // just take the rotation value from the controller
            m_lastHeading = m_gyro.getYaw();

            // save off timestamp to counter snapback
            m_lastHeadingTimestamp = Timer.getFPGATimestamp();
            
            gyroLast = m_gyro.getYaw();
            
            // square rotation value to give it more control at lower values
            // but keep the same sign since a negative squared is positive
            Rotation = Math.signum(Rotation) * Math.pow(Rotation, 2);
        }
        else
        {
            // maintain angle
            
            // delay the stay at angle to prevent snapback
            if((Timer.getFPGATimestamp() - m_lastHeadingTimestamp) > HEADING_TIMEOUT)
            {
                SmartDashboard.putString("Drive Mode", "Stay At Angle");
                
                // this should keep us facing the same direction
                
                // set the rotation using a PID controller based on current robot
                // heading and new desired heading
                Error = -Utilities.wrapToRange(m_lastHeading - m_gyro.getYaw(), -180, 180);
                double tempRotation = m_chassisAngleController.update(Error);
                
                // add a deadband to hopefully help with wheel lock after stopping
                SmartDashboard.putNumber("MaintainRotation", tempRotation);
                if(tempRotation > AUTO_ROTATION_MIN)
                {
                    Rotation = tempRotation;
                }
            }
            else
            {
                // save off the latest heading until the timeout
                m_lastHeading = m_gyro.getYaw();
            }
        }

        SmartDashboard.putNumber("Rotation Error", Error);
        
        // now update the drive
        return drive(Velocity, Rotation);
    }

    /**
     * Updates the chassis for Halo Drive from {@link Vector} type of velocity
     * 
     * @param Velocity robot's velocity using {@link Vector} type
     * @param Rotation robot's rotational movement, -1 to 1 rad/s
     * @return Array of {@link Vector} of the actual readings from the wheels
     */
    protected Vector[] drive(Vector Velocity, double Rotation)
    {
        SmartDashboard.putNumber("Velocity X", Velocity.getX());
        SmartDashboard.putNumber("Velocity Y", Velocity.getY());
        SmartDashboard.putNumber("Rotation", Rotation);
        
        // if we're relative to the field, we need to adjust the movement vector
        // based on the gyro heading
        if (m_fieldRelative)
        {
            SmartDashboard.putNumber("FieldRelativePre", Velocity.getAngle());
            Velocity.setAngle(adjustAngleFromGyro(Velocity.getAngle()));
            SmartDashboard.putNumber("FieldRelativePost", Velocity.getAngle());
        }


        return setWheelVectors(Velocity, Rotation);
    }

    /**
     * Scale the wheel vectors based on max available velocity, adjust for
     * rotation rate, then set/update the desired vectors individual wheels
     * 
     * @param RobotVelocity robot's velocity using {@link Vector} type; max speed is 1.0
     * @param RobotRotation robot's rotational movement; max rotation speed is -1 or 1
     * @return Array of {@link Vector} of the actual readings from the wheels
     */
    protected Vector[] setWheelVectors(Vector RobotVelocity,
            double RobotRotation)
    {
        Vector[] WheelsUnscaled = new Vector[SwerveConstants.WHEEL_COUNT];
        Vector[] WheelsActual = new Vector[SwerveConstants.WHEEL_COUNT];
        double MaxWantedVeloc = 0;

        // set limitations on speed
        if (RobotVelocity.getMag() > 1.0)
        {
            RobotVelocity.setMag(1.0);
        }
        
        // by squaring the magnitude, we get more fine adjustments at low speed
        // but keep the sign since negative squared is positive
        RobotVelocity.setMag(Math.signum(RobotVelocity.getMag()) * Math.pow(RobotVelocity.getMag(), 2));

        // limit before slowing speed so it runs using the original values
        // set limitations on rotation,
        // so if driving full speed it doesn't take priority
        if(ENABLE_ROTATION_LIMIT)
        {
            m_minRotationAdjust = Preferences.getInstance().getDouble("MinRotationAdjust", m_minRotationAdjust);
            double RotationAdjust = Math.min(1 - RobotVelocity.getMag() + m_minRotationAdjust, 1);
            RobotRotation = Utilities.clampToRange(RobotRotation, -RotationAdjust, RotationAdjust);
            RobotRotation *= m_rotationRateAdjust;
        }
        
        if(ENABLE_CRAWL_MODE)
        {
            double crawlSpeed = Preferences.getInstance().getDouble("DRIVE_SPEED_CRAWL", SwerveConstants.DRIVE_SPEED_CRAWL);
            
            // scale m_crawlMode from 0 and 1 to crawlSpeed and 1
            double scale = Utilities.scaleToRange(m_crawlMode, 0, 1, crawlSpeed, 1);

            // then scale back both rotation and velocity
            RobotRotation *= scale;
            RobotVelocity.setMag(RobotVelocity.getMag() * scale);
        }
        
        if(ENABLE_ACCELERATION_LIMIT)
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
                                                     * m_wheels[i].getPosition().getY(),
                                           RobotVelocity.getY()
                                                     + RobotRotation
                                                     * m_wheels[i].getPosition().getX());

            if (WheelsUnscaled[i].getMag() >= MaxWantedVeloc)
            {
                MaxWantedVeloc = WheelsUnscaled[i].getMag();
            }
        }

        double VelocityRatio = 1;
        
        if(ENABLE_VELOCITY_LIMIT)
        {
            // grab max velocity from the dash
            m_maxAvailableVelocity = Preferences.getInstance().getDouble("MAX_ROBOT_VELOCITY", m_maxAvailableVelocity);
            
            // determine ratio to scale all wheel velocities by
            VelocityRatio = m_maxAvailableVelocity / MaxWantedVeloc;
    
            if (VelocityRatio > 1)
            {
                VelocityRatio = 1;
            }
        }
        
        boolean actualGearHigh = m_gearHigh ? SwerveConstants.SHIFTER_DEFAULT_HIGH : !SwerveConstants.SHIFTER_DEFAULT_HIGH;
        
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
        m_maxAcceleration = Preferences.getInstance().getDouble("MAX_ACCELERATION", m_maxAcceleration);

        // determine if we are accelerating/decelerating too slow
        if (Math.abs(delta.getMag()) > m_maxAcceleration * TimeDelta)
        {
            // if we are, slow that down by the MaxAcceleration value
            delta.setMag(m_maxAcceleration * TimeDelta);
            robotVelocity = m_lastVelocity.add(delta);
        }

        return robotVelocity;
    }

    /**
     * Adjust the new angle based on the Gyroscope angle
     * 
     * @param Angle new desired angle
     * @return adjusted angle
     */
    private double adjustAngleFromGyro(double Angle)
    {
        // adjust the desired angle based on the robot's current angle
        double AdjustedAngle = Angle + m_gyro.getYaw();

        // Wrap to fit in the range -180 to 180
        return Utilities.wrapToRange(AdjustedAngle, -180, 180);
    }


    /**
     * get the last heading used for the robot. If free spinning, this will
     * constantly update
     * 
     * @return angle in degrees of the robot heading, relative to the field
     */
    public double getLastHeading()
    {
        return m_lastHeading;
    }
    
    /**
     * get the last movement vector of the robot, relative to the robot heading.
     * the adjustment for field relative mode, if applicable, has already been taken
     * into consideration
     * @return movement vector relative to the robot heading
     */
    public Vector getLastVector()
    {
        return m_lastVelocity;
    }

    /**
     * Set the shifting gear
     * 
     * @param GearHigh
     *            if true, shift to high gear, else low gear
     */
     public void setGearHigh(boolean GearHigh)
     {
         this.m_gearHigh = GearHigh;
     }

    /**
     * Get the shifting gear
     * 
     * @return true if currently in high gear, else false
     */
     public boolean getGearHigh()
     {
         return m_gearHigh;
     }

    /**
     * Get the actual reading of a wheel
     * 
     * @param index
     *            Index of the wheel
     * @return Actual reading of the wheel
     */
    public Vector getWheelActual(int index)
    {
        return m_wheels[index].getActual();
    }

    /**
     * Get the Gyro object
     * 
     * @return Gyro object
     */
    public Gyro getGyro()
    {
        return m_gyro;
    }

    /**
     * Get the SwerveWheel object for the specified index
     * 
     * @param index
     *            of wheel to get
     * @return SwerveWheel object
     */
    public SwerveWheel getWheel(int index)
    {
        return m_wheels[index];
    }

    public double getCrawlMode()
    {
        return m_crawlMode;
    }

    /**
     * Set the speed scaling value for crawl mode
     * @param crawlMode 0 to 1, directly proportional to speed sent to wheels
     */
    public void setCrawlMode(double crawlMode)
    {
        m_crawlMode = crawlMode;
    }
    
    /**
     * Do a full wheel calibration, adjusting the angles by the specified values,
     * and save the values for use
     * @param speed speed value to test against, 0-1
     * @param values array of values, 0-1, to adjust the wheel angle offsets
     */
    public void fullWheelCalibration(double speed, double values[], boolean save)
    {
        
        Vector vector = Vector.NewFromMagAngle(speed, 0);
        
        for(int i = 0; i < SwerveConstants.WHEEL_COUNT; i++)
        {
            m_wheels[i].updateAngleOffset(values[i] * 180.0);
            m_wheels[i].setDesired(vector, false, false);
            
            if(save)
            {
                m_wheels[i].saveAngleOffset(values[i] * 180.0);
            }
        }
    }
}
