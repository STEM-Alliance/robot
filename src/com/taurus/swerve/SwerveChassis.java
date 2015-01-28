/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus.swerve;

import com.kauailabs.nav6.frc.IMU;
import com.taurus.Utilities;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Swerve chassis implementation
 * 
 * @author Team 4818 Taurus Robotics
 */
public class SwerveChassis {
    public double MaxAvailableVelocity = 1;

    private SwerveWheel[] Wheels;

    private boolean FieldRelative;
    private boolean GearHigh;
    private boolean Brake;
    private double LastHeading;

    private PIController ChassisAngleController;
    public double ChassisP = 1.3 / 180; // Full speed rotation at error of 90
                                        // degrees.
    public double ChassisI = 0;
    private IMU Gyro;
    SerialPort serial_port;
    private double MinRotationAdjust = .3;

    /**
     * sets up individual wheels and their positions relative to robot center
     */
    public SwerveChassis()
    {
        ChassisAngleController = new PIController(ChassisP, ChassisI, 1.0);

        Wheels = new SwerveWheel[SwerveConstants.WheelCount];

        try
        {
            serial_port = new SerialPort(57600, SerialPort.Port.kMXP);
            byte update_rate_hz = 100;
            Gyro = new IMU(serial_port, update_rate_hz);
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
                    SwerveConstants.WheelOrientationAngle[i],
                    SwerveConstants.WheelEncoderPins[i],
                    SwerveConstants.WheelPotPins[i],
                    SwerveConstants.WheelDriveMotorPins[i],
                    SwerveConstants.WheelAngleMotorPins[i],
                    SwerveConstants.WheelShiftServoPins[i],
                    SwerveConstants.WheelShiftServoVals[i]);
        }

    }

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

        SmartDashboard.putNumber("Last Heading", LastHeading);
        SmartDashboard.putNumber("AngleDrive.error", Error);
        SmartDashboard.putNumber("AngleDrive.rotation", Rotation);

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

        UpdateShifter();

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
        SwerveVector[] WheelsUnscaled = new SwerveVector[SwerveConstants.WheelCount]; // Unscaled
                                                                                      // Wheel
                                                                                      // Velocities
        SwerveVector[] WheelsActual = new SwerveVector[SwerveConstants.WheelCount]; // Actual
                                                                                    // Wheel
                                                                                    // Velocities

        double MaxWantedVeloc = 0;

        // set limitations on speed
        if (RobotVelocity.getMag() > 1.0)
        {
            RobotVelocity.setMag(1.0);
        }
        double RotationAdjust = Math.min(1 - RobotVelocity.getMag()
                + MinRotationAdjust, 1);
        // set limitations on rotation
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
                    .setDesired(WheelScaled, GearHigh, Brake);
        }

        return WheelsActual;
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

    public void ZeroGyro()
    {
        Gyro.zeroYaw();
        LastHeading = 0;
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
        SmartDashboard.putBoolean("Brake", this.Brake);
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
        SmartDashboard.putBoolean("Field Relative", FieldRelative);
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
    public IMU getGyro()
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
}
