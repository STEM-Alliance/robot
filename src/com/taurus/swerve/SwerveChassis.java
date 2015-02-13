/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus.swerve;

import com.taurus.PIDController;
import com.taurus.Utilities;
import com.taurus.controller.Controller;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Swerve chassis implementation
 * 
 * @author Team 4818 Taurus Robotics
 */
public class SwerveChassis extends Subsystem {

    private SwerveWheel[] Wheels;

    private boolean FieldRelative;
//    private boolean GearHigh;
    private boolean Brake;
    private double LastHeading;

    private PIDController ChassisAngleController;
    private double ChassisP = 1.3 / 180; // Full speed rotation at error of 90
                                         // degrees.
    private double ChassisI = 0;
    private double ChassisD = 0;
    
    private SwerveIMU Gyro;
    SerialPort serial_port;
    
    private double MinRotationAdjust = .3;
    private double MaxAcceleration = 2;  // Smaller is slower acceleration
    private double MaxAvailableVelocity = 1;

    private SwerveVector LastVelocity;

    private double lastVelocityTimestamp;

    private DriveScheme driveScheme;

    /**
     * sets up individual wheels and their positions relative to robot center
     */
    public SwerveChassis()
    {
        ChassisAngleController = new PIDController(ChassisP, ChassisI, ChassisD, 1.0);
        
        LastVelocity = new SwerveVector(0,0);

        Wheels = new SwerveWheel[SwerveConstants.WheelCount];
        
        driveScheme = new DriveScheme();

        try
        {
            serial_port = new SerialPort(57600, SerialPort.Port.kMXP);
            byte update_rate_hz = 100;
            Gyro = new SwerveIMU(serial_port, update_rate_hz);
        }
        catch (Exception ex)
        {
        }

        if (Gyro != null)
        {
            LiveWindow.addSensor("IMU", "Gyro", Gyro);
        }

        // {x, y}, Orientation, {EncoderA, EncoderB}, Pot, Drive, Angle
        for (int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            Wheels[i] = new SwerveWheel("wheel" + i,
                    SwerveConstants.WheelPositions[i],
                    Application.prefs.getDouble("Wheel_Orientation_" + i, SwerveConstants.WheelOrientationAngle[i]),
//                    SwerveConstants.WheelEncoderPins[i],
                    SwerveConstants.WheelPotPins[i],
                    SwerveConstants.WheelDriveMotorAddress[i],
                    SwerveConstants.WheelAngleMotorPins[i]);
//                    SwerveConstants.WheelShiftServoPins[i],
//                    SwerveConstants.WheelShiftServoVals[i]);
        }

    }
    
    /**
     * Run the normal drive setup, using inputs from the controller
     * @param controller
     */
    public void run(Controller controller)
    {
        for (int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            Wheels[i].setOrientation(Application.prefs.getDouble("Wheel_Orientation_" + i,
                    SwerveConstants.WheelOrientationAngle[i]));
        }
        
        // Use the Joystick inputs to update the drive system
        switch (driveScheme.get())
        {
            case DriveScheme.ANGLE_DRIVE:
                UpdateDrive(controller.getAngleDrive_Velocity(), 0,
                        controller.getAngleDrive_Heading());
                break;

            case DriveScheme.HALO_DRIVE:
                UpdateHaloDrive(controller.getHaloDrive_Velocity(),
                        controller.getHaloDrive_Rotation());
                break;

            default:
            case DriveScheme.COMBO_DRIVE:
                UpdateDrive(controller.getHaloDrive_Velocity(),
                        controller.getHaloDrive_Rotation(),
                        controller.getDPad());
                break;
        }

        // set various options for the chassis
//        setGearHigh(controller.getHighGearEnable());
        setBrake(controller.getBrake());
        setFieldRelative(controller.getFieldRelative());
        if (controller.getResetGyro())
        {
            ZeroGyro();
        }
    }

    /**
     * Main drive update function, allows for xy movement, yaw rotation, and 
     * turn to angle/heading
     * @param Velocity vector of xy movement of robot
     * @param Rotation robot's rotational movement, -1 to 1 rad/s
     * @param Heading 0-360 of angle to turn to, -1 if not in use
     * @return actual wheel readings
     */
    public SwerveVector[] UpdateDrive(SwerveVector Velocity, double Rotation,
            double Heading)
    {
        double Error = 0;
        if (Math.abs(Rotation) < .1)
        {
            // if we're not spinning
            if (Heading != -1)
            {
                // pressing on the dpad
                // set the rotation using a PI controller based on current robot
                // heading and new desired heading
                Error = Utilities.wrapToRange(Heading - Gyro.getYaw(), -180,
                        180);
                Rotation = ChassisAngleController.update(Error,
                        Timer.getFPGATimestamp());
                LastHeading = Heading;
            }
            else
            {
                // not pressing on dpad
                // set the rotation using a PI controller based on current robot
                // heading and new desired heading
                Error = Utilities.wrapToRange(LastHeading - Gyro.getYaw(),
                        -180, 180);
                Rotation = ChassisAngleController.update(Error,
                        Timer.getFPGATimestamp());
            }
        }
        else
        {
            // spinning
            LastHeading = Gyro.getYaw();
        }

        //SmartDashboard.putNumber("AngleDrive.error", Error);
        //SmartDashboard.putNumber("AngleDrive.rotation", Rotation);

        return UpdateHaloDrive(Velocity, Rotation);
    }

    /**
     * Updates the chassis for Halo Drive from SwerveVector type of velocity
     * 
     * @param Velocity
     *            robot's velocity using SwerveVector type
     * @param Rotation
     *            robot's rotational movement, -1 to 1 rad/s
     * @return Array of SwerveVectors of the actual readings from the wheels
     */
    public SwerveVector[] UpdateHaloDrive(SwerveVector Velocity, double Rotation)
    {
        if (FieldRelative)
        {
            Velocity.setAngle(adjustAngleFromGyro(Velocity.getAngle()));
        }

//        UpdateShifter();

        return setWheelVectors(Velocity, Rotation);
    }

    /**
     * Scale the wheel vectors based on max available velocity, adjust for
     * rotation rate, then set/update the desired vectors individual wheels
     * 
     * @param RobotVelocity
     *            robot's velocity using SwerveVector type; max speed is 1.0
     * @param RobotRotation
     *            robot's rotational movement; max rotation speed is -1 or 1
     * @return Array of SwerveVectors of the actual readings from the wheels
     */
    private SwerveVector[] setWheelVectors(SwerveVector RobotVelocity,
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
        
        RobotVelocity = restrictVelocity(RobotVelocity);

        // set limitations on rotation,
        // so if driving full speed it doesn't take priority
        double RotationAdjust = Math.min(1 - RobotVelocity.getMag()
                + MinRotationAdjust, 1);
        RobotRotation = Utilities.clampToRange(RobotRotation, -RotationAdjust,
                RotationAdjust);

        // calculate vectors for each wheel
        for (int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            // calculate
            WheelsUnscaled[i] = new SwerveVector(RobotVelocity.getX()
                    + RobotRotation * Wheels[i].getPosition().getY(),
                    RobotVelocity.getY() + RobotRotation
                            * Wheels[i].getPosition().getX());

            if (WheelsUnscaled[i].getMag() >= MaxWantedVeloc)
            {
                MaxWantedVeloc = WheelsUnscaled[i].getMag();
            }
        }

        // grab max velocity from the dash
        MaxAvailableVelocity = Application.prefs.getDouble("MAX_ROBOT_VELOCITY", MaxAvailableVelocity);

        // determine ratio to scale all wheel velocities by
        double Ratio = MaxAvailableVelocity / MaxWantedVeloc;

        if (Ratio > 1)
        {
            Ratio = 1;
        }

        for (int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            // Scale values for each wheel
            SwerveVector WheelScaled = SwerveVector.NewFromMagAngle(
                    WheelsUnscaled[i].getMag() * Ratio,
                    WheelsUnscaled[i].getAngle());

            // Set the wheel speed
            WheelsActual[i] = Wheels[i]
                    .setDesired(WheelScaled,/* GearHigh,*/ Brake);
        }

        return WheelsActual;
    }

    /**
     * Returns the velocity restricted by the maximum acceleration
     * @param robotVelocity
     * @return
     */
    private SwerveVector restrictVelocity(SwerveVector robotVelocity)
    {
        double TimeDelta = Timer.getFPGATimestamp() - lastVelocityTimestamp;
        lastVelocityTimestamp = Timer.getFPGATimestamp();
        
        // get the difference between last velocity and this velocity
        SwerveVector delta = robotVelocity.subtract(LastVelocity);
        
        // grab the max acceleration value from the dash
        MaxAcceleration = Application.prefs.getDouble("MAX_ACCELERATION", MaxAcceleration); 

        
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
     * @param Angle
     *            new desired angle
     * @return adjusted angle
     */
    private double adjustAngleFromGyro(double Angle)
    {
        // adjust the desired angle based on the robot's current angle
        double AdjustedAngle = Angle - Gyro.getYaw();

        // Wrap to fit in the range -180 to 180
        return Utilities.wrapToRange(AdjustedAngle, -180, 180);
    }

    /**
     * Zero the yaw of the gyroscope
     */
    public void ZeroGyro()
    {
        Gyro.zeroYaw();
        LastHeading = 0;
    }
    
    /**
     * Set the Gyro to use a new zero value
     * 
     * @param yaw angle to offset by
     */
    public void SetGyroZero(float yaw)
    {
        Gyro.setZero(yaw);
    }

    /**
     * Set the chassis's brake
     * 
     * @param Brake
     *            if true, set the brake, else release brake
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
     * get the last heading used for the robot. If free spinning,
     * this will constantly update
     * @return angle in degrees
     */
    public double getLastHeading()
    {
        return LastHeading;
    }

    /**
     * Update the Shifting/Gear servo
     */
//    public void UpdateShifter()
//    {
//        // switch to the desired gear
//        if (GearHigh)
//        {
//            SmartDashboard.putString("Gear", "High");
//
//        }
//        else
//        {
//            SmartDashboard.putString("Gear", "Low");
//        }
//    }

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
//    public void setGearHigh(boolean GearHigh)
//    {
//        this.GearHigh = GearHigh;
//    }

    /**
     * Get the shifting gear
     * 
     * @return true if currently in high gear, else false
     */
//    public boolean getGearHigh()
//    {
//        return GearHigh;
//    }

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
    public SwerveIMU getGyro()
    {
        return Gyro;
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

    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
    }
}
