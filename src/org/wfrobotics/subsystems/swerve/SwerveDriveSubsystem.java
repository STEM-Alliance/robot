/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wfrobotics.subsystems.swerve;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.commands.drive.*;
import org.wfrobotics.hardware.Gyro;
import org.wfrobotics.robot.RobotMap;
import org.wfrobotics.subsystems.DriveSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Swerve chassis implementation
 * 
 * @author Team 4818 WFRobotics
 */
public class SwerveDriveSubsystem extends DriveSubsystem {

    protected SwerveWheel[] Wheels;

    protected Gyro navxMXP;
    
    private boolean FieldRelative;
    private double LastHeading;
    private boolean GearHigh;
    private boolean Brake;

    private PIDController ChassisAngleController;
    private double ChassisP = 2.5 / 180;
    private double ChassisI = 0;
    private double ChassisD = 0;

    private double CrawlMode = 0.0;
    private final boolean ENABLE_CRAWL_MODE = true;
    
    private double MaxAcceleration = 2; // Smaller is slower acceleration
    private final boolean ENABLE_ACCELERATION_LIMIT = false;
    
    private double MaxAvailableVelocity = 1;
    private final boolean ENABLE_VELOCITY_LIMIT = false;

    private double MinRotationAdjust = .3;
    protected double RotationRateAdjust = 1;
    private final boolean ENABLE_ROTATION_LIMIT = false;

    private Vector LastVelocity;
    private double lastVelocityTimestamp;

    /**
     * sets up individual wheels and their positions relative to robot center
     */
    public SwerveDriveSubsystem()
    {
        this(Gyro.getInstance());
    }
    
    /**
     * sets up individual wheels and their positions relative to robot center
     */
    public SwerveDriveSubsystem(Gyro gyro)
    {
        ChassisAngleController = new PIDController(ChassisP, ChassisI, ChassisD, 1.0);

        LastVelocity = new Vector(0, 0);

        Wheels = new SwerveWheel[SwerveConstants.WheelCount];

        // creat the wheel objects
        for (int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            // use SRXs for the angle inputs
            Wheels[i] = new SwerveWheel(i,
                    SwerveConstants.WheelPositions[i],
                    //RobotMap.ANG_SWERVE_ANGLE[i],
                    RobotMap.CAN_SWERVE_DRIVE_TALONS[i],
                    RobotMap.CAN_SWERVE_ANGLE_TALONS[i],
                    RobotMap.PWM_SWERVE_SHIFT_SERVOS[i],
                    SwerveConstants.WheelShiftServoVals[i],
                    SwerveConstants.WheelAngleCalibrationPins[i]);
        }

        // Setup gyro
        try {
            navxMXP = gyro;
        } catch (RuntimeException ex ) {
            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        }
    }
    
    /**
     * set the default command
     */
    public void initDefaultCommand() 
    {
        setDefaultCommand(new DriveSwerveHalo());
    }
    
    public void free()
    {
        for (int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            Wheels[i].free();
        }
    }
    

    @Override
    public void driveTank(double right, double left)
    {
        //TODO?
    }

    @Override
    public void drivePolar(double magnitude, double angle, double rotation)
    {
        UpdateDrive(Vector.NewFromMagAngle(magnitude, angle), rotation, navxMXP.getYaw());
    }

    @Override
    public void driveVector(Vector velocity, double rotation)
    {
        UpdateDrive(velocity, rotation, navxMXP.getYaw());
    }

    @Override
    public void driveCartesian(double x, double y, double rotation)
    {
        UpdateDrive(new Vector(x,y), rotation, navxMXP.getYaw());
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
    public Vector[] UpdateDrive(Vector Velocity, double Rotation,
            double Heading)
    {
        double Error = 0;
        
        // determine which drive mode to use between
        if (Math.abs(Rotation) < .25)
        {
            // if we're not spinning
            if (Heading != -1)
            {
                // pressing on the dpad
                SmartDashboard.putString("Drive Mode", "Rotate To Heading");
                
                // this should snap us to a specific angle
                
                // set the rotation using a PID controller based on current robot
                // heading and new desired heading
                Error = Utilities.wrapToRange(Heading - navxMXP.getYaw(), -180, 180);
                Rotation = ChassisAngleController.update(Error);
                LastHeading = Heading;
            }
            else
            {
                // not pressing on dpad
                SmartDashboard.putString("Drive Mode", "Stay At Angle");
                
                // this should keep us facing the same direction
                
                // set the rotation using a PID controller based on current robot
                // heading and new desired heading
                Error = -Utilities.wrapToRange(LastHeading - navxMXP.getYaw(), -180, 180);
                Rotation = ChassisAngleController.update(Error);
            }
        }
        else
        {
            // spinning
            SmartDashboard.putString("Drive Mode", "Spinning");
            
            // just take the rotation value from the controller
            
            LastHeading = navxMXP.getYaw();
        }

        SmartDashboard.putNumber("Rotation Error", Error);
        
        // now update the drive
        return UpdateHaloDrive(Velocity, Rotation);
    }

    /**
     * Updates the chassis for Halo Drive from {@link Vector} type of velocity
     * 
     * @param Velocity robot's velocity using {@link Vector} type
     * @param Rotation robot's rotational movement, -1 to 1 rad/s
     * @return Array of {@link Vector} of the actual readings from the wheels
     */
    public Vector[] UpdateHaloDrive(Vector Velocity, double Rotation)
    {
        SmartDashboard.putNumber("Velocity X", Velocity.getX());
        SmartDashboard.putNumber("Velocity Y", Velocity.getY());
        SmartDashboard.putNumber("Rotation", Rotation);
        SmartDashboard.putBoolean("FieldRelative", FieldRelative);
        
        // if we're relative to the field, we need to adjust the movement vector
        // based on the gyro heading
        if (FieldRelative)
        {
            Velocity.setAngle(adjustAngleFromGyro(Velocity.getAngle()));
        }

        // update the shifters as needed
        UpdateShifter();

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
        Vector[] WheelsUnscaled = new Vector[SwerveConstants.WheelCount];
        Vector[] WheelsActual = new Vector[SwerveConstants.WheelCount];
        double MaxWantedVeloc = 0;

        // set limitations on speed
        if (RobotVelocity.getMag() > 1.0)
        {
            RobotVelocity.setMag(1.0);
        }
        
        // by squaring the magnitude, we get more fine adjustments at low speed
        RobotVelocity.setMag(RobotVelocity.getMag() * RobotVelocity.getMag());

        // limit before slowing speed so it runs using the original values
        // set limitations on rotation,
        // so if driving full speed it doesn't take priority
        if(ENABLE_ROTATION_LIMIT)
        {
            MinRotationAdjust = Preferences.getInstance().getDouble("MinRotationAdjust", MinRotationAdjust);
            double RotationAdjust = Math.min(1 - RobotVelocity.getMag() + MinRotationAdjust, 1);
            RobotRotation = Utilities.clampToRange(RobotRotation, -RotationAdjust, RotationAdjust);
        }
        
        if(ENABLE_CRAWL_MODE)
        {
            double crawlSpeed = Preferences.getInstance().getDouble("Drive_Speed_Crawl", SwerveConstants.DriveSpeedCrawl);
            
            RobotRotation *= (crawlSpeed + (1 - crawlSpeed) * getCrawlMode() * .9);
            
            // scale the speed down
            RobotVelocity.setMag(RobotVelocity.getMag() * (crawlSpeed + (1 - crawlSpeed) * getCrawlMode()));
            RobotRotation *= SwerveConstants.DriveSpeedNormal;
            RobotVelocity.setMag(RobotVelocity.getMag() * SwerveConstants.DriveSpeedNormal);
        }
        
        if(ENABLE_ROTATION_LIMIT)
        {
            RobotRotation *= RotationRateAdjust;
        }
        
        if(ENABLE_ACCELERATION_LIMIT)
        {
            RobotVelocity = restrictVelocity(RobotVelocity);
        }
        
        SmartDashboard.putNumber("Drive X", RobotVelocity.getX());
        SmartDashboard.putNumber("Drive Y", RobotVelocity.getY());
        SmartDashboard.putNumber("Drive Mag", RobotVelocity.getMag());
        SmartDashboard.putNumber("Drive Ang", RobotVelocity.getAngle());
        SmartDashboard.putNumber("Drive R", RobotRotation);

        // calculate vectors for each wheel
        for (int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            // calculate
            WheelsUnscaled[i] = new Vector(RobotVelocity.getX()
                                                     - RobotRotation
                                                     * Wheels[i].getPosition().getY(),
                                                 RobotVelocity.getY()
                                                     + RobotRotation
                                                     * Wheels[i].getPosition().getX());

            if (WheelsUnscaled[i].getMag() >= MaxWantedVeloc)
            {
                MaxWantedVeloc = WheelsUnscaled[i].getMag();
            }
        }

        double VelocityRatio = 1;
        
        if(ENABLE_VELOCITY_LIMIT)
        {
            // grab max velocity from the dash
            MaxAvailableVelocity = Preferences.getInstance().getDouble("MAX_ROBOT_VELOCITY", MaxAvailableVelocity);
            
            // determine ratio to scale all wheel velocities by
            VelocityRatio = MaxAvailableVelocity / MaxWantedVeloc;
    
            if (VelocityRatio > 1)
            {
                VelocityRatio = 1;
            }
        }
        
        if(SwerveConstants.WheelShiftDefaultHigh) 
            GearHigh = !GearHigh;
        
        for (int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            // Scale values for each wheel
            Vector WheelScaled = Vector.NewFromMagAngle(WheelsUnscaled[i].getMag() * VelocityRatio, WheelsUnscaled[i].getAngle());

            // Set the wheel speed
            WheelsActual[i] = Wheels[i].setDesired(WheelScaled, GearHigh, Brake);
        }

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
        double TimeDelta = Timer.getFPGATimestamp() - lastVelocityTimestamp;
        lastVelocityTimestamp = Timer.getFPGATimestamp();

        // get the difference between last velocity and this velocity
        Vector delta = robotVelocity.subtract(LastVelocity);

        // grab the max acceleration value from the dash
        MaxAcceleration = Preferences.getInstance().getDouble("MAX_ACCELERATION", MaxAcceleration);

        // determine if we are accelerating/decelerating too slow
        if (delta.getMag() > MaxAcceleration * TimeDelta)
        {
            // if we are, slow that down by the MaxAcceleration value
            delta.setMag(MaxAcceleration * TimeDelta);
            robotVelocity = LastVelocity.add(delta);
        }

        LastVelocity = robotVelocity;
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
        double AdjustedAngle = Angle - navxMXP.getYaw();

        // Wrap to fit in the range -180 to 180
        return Utilities.wrapToRange(AdjustedAngle, -180, 180);
    }

    /**
     * Zero the yaw of the gyroscope
     */
    public void ZeroGyro()
    {
        navxMXP.zeroYaw();
        LastHeading = 0;
    }

    /**
     * Set the Gyro to use a new zero value
     * 
     * @param yaw angle to offset by
     */
    public void SetGyroZero(float yaw)
    {
        navxMXP.setZero(yaw);
        LastHeading = yaw;
    }
    
    /**
     * Set the chassis's brake
     * 
     * @param Brake if true, set the brake, else release brake
     */
    public void setBrake(boolean Brake)
    {
        this.Brake = Brake;
    }

    /**
     * Get the chassis's brake
     * 
     * @return true if brake is set, else false
     */
    public boolean getBrake()
    {
        return Brake;
    }

    /**
     * get the last heading used for the robot. If free spinning, this will
     * constantly update
     * 
     * @return angle in degrees
     */
    public double getLastHeading()
    {
        return LastHeading;
    }

    /**
     * Update the Shifting/Gear servo
     */
     public void UpdateShifter()
     {
         // switch to the desired gear
         if (GearHigh)
         {
             SmartDashboard.putString("Gear", "High");
         }
         else
         {
             SmartDashboard.putString("Gear", "Low");
         }
     }

    /**
     * Set if driving is field relative or robot relative
     * 
     * @param FieldRelative
     */
    public void setFieldRelative(boolean FieldRelative)
    {
        this.FieldRelative = FieldRelative;
    }

    /**
     * Get if driving is field relative or robot relative
     * 
     * @return
     */
    public boolean getFieldRelative()
    {
        return FieldRelative;
    }

    /**
     * Set the shifting gear
     * 
     * @param GearHigh
     *            if true, shift to high gear, else low gear
     */
     public void setGearHigh(boolean GearHigh)
     {
     this.GearHigh = GearHigh;
     }

    /**
     * Get the shifting gear
     * 
     * @return true if currently in high gear, else false
     */
     public boolean getGearHigh()
     {
     return GearHigh;
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
        return Wheels[index].getActual();
    }

    /**
     * Get the Gyro object
     * 
     * @return Gyro object
     */
    public Gyro getGyro()
    {
        return navxMXP;
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
        return Wheels[index];
    }


    public double getCrawlMode()
    {
        return CrawlMode;
    }

    public void setCrawlMode(double crawlMode)
    {
        CrawlMode = crawlMode;
    }

}
