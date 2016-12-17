/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.wfrobotics.subsystems.swerve;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.commands.drive.DriveSwerveCombo;
import org.wfrobotics.commands.drive.DriveSwerveHalo;
import org.wfrobotics.commands.drive.DriveSwerveSingleWheelTest;
import org.wfrobotics.commands.drive.DriveSwerveWheelCalibration;
import org.wfrobotics.commands.drive.DriveTank;
import org.wfrobotics.hardware.Gyro;
import org.wfrobotics.robot.RobotMap;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Swerve chassis implementation
 * 
 * @author Team 4818 WFRobotics
 */
public class SwerveDriveSubsystem extends Subsystem {

    protected SwerveWheel[] Wheels;

    protected Gyro navxMXP;
    
    private boolean FieldRelative;
    private double LastHeading;
    private boolean GearHigh;
    private boolean Brake;
    private double CrawlMode = 0.0;

    private PIDController ChassisAngleController;
    private double ChassisP = 2.5 / 180; // /1.3 / 180; // Full speed rotation
                                         // at error of 90
                                         // degrees.
    private double ChassisI = 0;
    private double ChassisD = 0;

    private double MinRotationAdjust = .3;
    private double MaxAcceleration = 2; // Smaller is slower acceleration
    private double MaxAvailableVelocity = 1;
    
    protected double RotationRateAdjust = 1;

    private SwerveVector LastVelocity;
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

        LastVelocity = new SwerveVector(0, 0);

        Wheels = new SwerveWheel[SwerveConstants.WheelCount];

        // {x, y}, Orientation, {EncoderA, EncoderB}, Pot, Drive, Angle
        for (int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            Wheels[i] = new SwerveWheel(i,
                    SwerveConstants.WheelPositions[i],
//                    SwerveConstants.WheelEncoderPins[i],
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
        // Set the default command for a subsystem here.
        setDefaultCommand(new DriveSwerveHalo());
    }
    
    public void free()
    {
//        for (int i = 0; i < SwerveConstants.WheelCount; i++)
//        {
//            Wheels[i].free();
//        }
    }
    
    /**
     * Main drive update function, allows for xy movement, yaw rotation, and
     * turn to angle/heading
     * 
     * @param Velocity {@link SwerveVector} of xy movement of robot
     * @param Rotation robot's rotational movement, -1 to 1 rad/s
     * @param Heading 0-360 of angle to turn to, -1 if not in use
     * @return actual wheel readings
     */
    public SwerveVector[] UpdateDrive(SwerveVector Velocity, double Rotation,
            double Heading)
    {
        double Error = 0;
        if (Math.abs(Rotation) < .25)
        {
            // if we're not spinning
            if (Heading != -1)
            {
                SmartDashboard.putString("Drive Mode", "Rotate To Heading");
                
                // pressing on the dpad
                // set the rotation using a PI controller based on current robot
                // heading and new desired heading
                Error = Utilities.wrapToRange(Heading - navxMXP.getYaw(), -180,
                                180);
                Rotation = ChassisAngleController.update(Error,
                                Timer.getFPGATimestamp());
                LastHeading = Heading;
            }
            else
            {
                SmartDashboard.putString("Drive Mode", "Stay At Angle");
                // not pressing on dpad
                // set the rotation using a PI controller based on current robot
                // heading and new desired heading
                Error = -Utilities.wrapToRange(LastHeading - navxMXP.getYaw(),
                                -180, 180);
                
                SmartDashboard.putNumber("Rotation Error", Error);
                
                Rotation = ChassisAngleController.update(Error,
                                Timer.getFPGATimestamp());
            }
        }
        else
        {
            SmartDashboard.putString("Drive Mode", "Spinning");
            // spinning
            LastHeading = navxMXP.getYaw();
        }


        return UpdateHaloDrive(Velocity, Rotation);
    }

    /**
     * Updates the chassis for Halo Drive from {@link SwerveVector} type of velocity
     * 
     * @param Velocity robot's velocity using {@link SwerveVector} type
     * @param Rotation robot's rotational movement, -1 to 1 rad/s
     * @return Array of {@link SwerveVector} of the actual readings from the wheels
     */
    public SwerveVector[] UpdateHaloDrive(SwerveVector Velocity, double Rotation)
    {
        SmartDashboard.putNumber("Velocity X", Velocity.getX());
        SmartDashboard.putNumber("Velocity Y", Velocity.getY());
        SmartDashboard.putNumber("Rotation", Rotation);
        SmartDashboard.putBoolean("FieldRelative", FieldRelative);
        
        if (FieldRelative)
        {
            Velocity.setAngle(adjustAngleFromGyro(Velocity.getAngle()));
        }

        UpdateShifter();

        return setWheelVectors(Velocity, Rotation);
    }

    /**
     * Scale the wheel vectors based on max available velocity, adjust for
     * rotation rate, then set/update the desired vectors individual wheels
     * 
     * @param RobotVelocity robot's velocity using {@link SwerveVector} type; max speed is 1.0
     * @param RobotRotation robot's rotational movement; max rotation speed is -1 or 1
     * @return Array of {@link SwerveVector} of the actual readings from the wheels
     */
    protected SwerveVector[] setWheelVectors(SwerveVector RobotVelocity,
            double RobotRotation)
    {
        SwerveVector[] WheelsUnscaled = new SwerveVector[SwerveConstants.WheelCount];
        SwerveVector[] WheelsActual = new SwerveVector[SwerveConstants.WheelCount];
        double MaxWantedVeloc = 0;

        // set limitations on speed
        if (RobotVelocity.getMag() > 1.0)
        {
            RobotVelocity.setMag(1.0);
        }
        
        RobotVelocity.setMag(RobotVelocity.getMag() * RobotVelocity.getMag());

        // limit before slowing speed so it runs using the original values
        // set limitations on rotation,
        // so if driving full speed it doesn't take priority
        MinRotationAdjust = Preferences.getInstance().getDouble("MinRotationAdjust", MinRotationAdjust);
        double RotationAdjust = Math.min(1 - RobotVelocity.getMag() + MinRotationAdjust, 1);
        RobotRotation = Utilities.clampToRange(RobotRotation, -RotationAdjust, RotationAdjust);

//        SmartDashboard.putNumber("Drive R pre", RobotRotation);
        
        double crawlSpeed = Preferences.getInstance().getDouble("Drive_Speed_Crawl", SwerveConstants.DriveSpeedCrawl);
        
        RobotRotation *= (crawlSpeed + (1 - crawlSpeed) * getCrawlMode() * .9);
        

//        SmartDashboard.putNumber("Drive R pre 2", RobotRotation);
        
        // scale the speed down
        RobotVelocity.setMag(RobotVelocity.getMag() * (crawlSpeed + (1 - crawlSpeed) * getCrawlMode()));
        RobotRotation *= SwerveConstants.DriveSpeedNormal;
        RobotVelocity.setMag(RobotVelocity.getMag() * SwerveConstants.DriveSpeedNormal);

        
        RobotRotation *= RotationRateAdjust;
        

        RobotVelocity = restrictVelocity(RobotVelocity);

        SmartDashboard.putNumber("Drive X", RobotVelocity.getX());
        SmartDashboard.putNumber("Drive Y", RobotVelocity.getY());
        SmartDashboard.putNumber("Drive Mag", RobotVelocity.getMag());
        SmartDashboard.putNumber("Drive Ang", RobotVelocity.getAngle());
        SmartDashboard.putNumber("Drive R", RobotRotation);

        // calculate vectors for each wheel
        for (int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            // calculate
            WheelsUnscaled[i] = new SwerveVector(RobotVelocity.getX()
                                                     + RobotRotation
                                                     * Wheels[i].getPosition().getY(),
                                                 RobotVelocity.getY()
                                                     + RobotRotation
                                                     * Wheels[i].getPosition().getX());

            if (WheelsUnscaled[i].getMag() >= MaxWantedVeloc)
            {
                MaxWantedVeloc = WheelsUnscaled[i].getMag();
            }
        }

        // grab max velocity from the dash
        MaxAvailableVelocity = Preferences.getInstance().getDouble("MAX_ROBOT_VELOCITY",
                        MaxAvailableVelocity);

        // determine ratio to scale all wheel velocities by
        double Ratio = MaxAvailableVelocity / MaxWantedVeloc;

        if (Ratio > 1)
        {
            Ratio = 1;
        }

        
        if(SwerveConstants.WheelShiftDefaultHigh) 
            GearHigh = !GearHigh;
        
        for (int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            // Scale values for each wheel
            SwerveVector WheelScaled =
                    SwerveVector.NewFromMagAngle(WheelsUnscaled[i].getMag() * Ratio,
                            WheelsUnscaled[i].getAngle());

            // Set the wheel speed
            WheelsActual[i] = Wheels[i].setDesired(WheelScaled, GearHigh, Brake);
        }

        return WheelsActual;
    }

    /**
     * Returns the velocity restricted by the maximum acceleration
     * 
     * @param robotVelocity
     * @return
     */
    protected SwerveVector restrictVelocity(SwerveVector robotVelocity)
    {
        double TimeDelta = Timer.getFPGATimestamp() - lastVelocityTimestamp;
        lastVelocityTimestamp = Timer.getFPGATimestamp();

        // get the difference between last velocity and this velocity
        SwerveVector delta = robotVelocity.subtract(LastVelocity);

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
    public SwerveVector getWheelActual(int index)
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
