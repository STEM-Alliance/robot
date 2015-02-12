/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus.swerve;

import com.taurus.MagnetoPot;
import com.taurus.Utilities;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Talon;

/**
 * Handle motor outputs and feedback for an individual wheel
 * 
 * @author Team 4818 Taurus Robotics
 */
public class SwerveWheel {
    String Name;

    // wheel stuff
    private SwerveVector WheelPosition; // wheel location from center of robot
    private SwerveVector WheelDesired; // wheel speed, x and y vals, hypotenuse
                                       // val, angle
    private SwerveVector WheelActual; // wheel speed, x and y vals, hypotenuse
                                      // val, angle

    private Servo Shifter;
    private int ShifterValueHigh;
    private int ShifterValueLow;
    private boolean HighGear;

    private boolean Brake;

    private double maxRotationSpeed = .75;

    private double AngleOrienation = 0;

    // motor
    private CANTalon MotorDrive;
    public Talon MotorAngle;

    // sensor
    public MagnetoPot AnglePot;
    //private Encoder DriveEncoder;

    // controller
    private SwerveAngleController AngleController;
//    private VelocityCalculator DriveEncoderFilter;
//    private PIController DriveEncoderController;

//    private static final double DriveP = 0.3;
//    private static final double DriveTI = 0.5; // seconds needed to equal a P
//                                               // term contribution
//    private static final double DriveI = 0;// 2 / DriveTI;

    // deadband
    private static final double MinSpeed = 0.08;

    /**
     * Set up the wheel with the specific IO and orientation on the robot
     * 
     * @param name
     *            Name of the wheel for display purposes
     * @param Position
     *            Wheel position relative to robot center as array
     * @param Orientation
     *            Angle of wheel relative to robot 0 angle in degrees
     * @param EncoderPins
     *            Pins for Speed Encoder input as array
     * @param PotPin
     *            Pin for Angle Potentiometer
     * @param DriveAddress
     *            Address for drive motor controller
     * @param AnglePin
     *            Pin for angle motor controller
     */
    public SwerveWheel(String name, double[] Position, double Orientation,
            int[] EncoderPins, int PotPin, int DriveAddress, int AnglePin,
            int ShiftPin, int[] ShiftVals)
    {
        Name = name;

        WheelPosition = new SwerveVector(Position);
        WheelActual = new SwerveVector(0, 0);
        WheelDesired = new SwerveVector(0, 0);
        MotorDrive = new CANTalon(DriveAddress);
        MotorAngle = new Talon(AnglePin);

        HighGear = true;
        Shifter = new Servo(ShiftPin);
        ShifterValueHigh = ShiftVals[0];
        ShifterValueLow = ShiftVals[1];

        //DriveEncoder = new Encoder(EncoderPins[0], EncoderPins[1]);
        //DriveEncoder.setDistancePerPulse(SwerveConstants.DriveEncoderRate);

//        DriveEncoderFilter = new VelocityCalculator();
//        DriveEncoderController = new PIController(DriveP, DriveI, 1.0);

        // AnglePot = new AnalogPotentiometer(PotPin, 360 + Math.abs(SpinMin) +
        // Math.abs(SpinMax), -SpinMin);
        AnglePot = new MagnetoPot(PotPin, 360);
        AngleController = new SwerveAngleController(name + ".ctl");

        AngleOrienation = Orientation;
    }

    /**
     * Set the desired wheel vector, auto updates the PID controllers
     * 
     * @param NewDesired
     * @param HighGear
     * @return Actual vector reading of wheel
     */
    public SwerveVector setDesired(SwerveVector NewDesired,
            boolean NewHighGear, boolean NewBrake)
    {
        WheelDesired = NewDesired;
        HighGear = NewHighGear;
        Brake = NewBrake;

        return updateTask();
    }

    /**
     * Get the desired vector (velocity and rotation) of this wheel instance
     * 
     * @return Desired vector
     */
    public SwerveVector getDesired()
    {
        return WheelDesired;
    }

    /**
     * Get the actual vector reading of the wheel
     * 
     * @return Actual vector reading of wheel
     */
    public SwerveVector getActual()
    {
     //   WheelActual.setMagAngle(DriveEncoder.getRate(), getAnglePotValue());
        WheelActual.setMagAngle(WheelDesired.getMag(), getAnglePotValue());
        return WheelActual;
    }

    /**
     * Get the wheel's position relative to robot center
     * 
     * @return Wheel position
     */
    public SwerveVector getPosition()
    {
        return WheelPosition;
    }

    public void setOrientation(double orientation)
    {
        this.AngleOrienation = orientation;
    }
    
    /**
     * Get whether the wheel is in high gear or low gear
     * 
     * @return Whether the wheel is in high gear
     */
    public boolean getIsHighGear()
    {
        return HighGear;
    }

    private void updateShifter()
    {
        if (HighGear)
        {
            Shifter.setAngle(ShifterValueHigh);

        }
        else
        {
            Shifter.setAngle(ShifterValueLow);
        }
    }

    /**
     * Get the angle of the potentiometer
     * 
     * @return
     */
    public double getAnglePotValue()
    {
        return AnglePot.get();
    }

    /**
     * Adjust the angle for the orientation value
     * 
     * @param angle
     * @return
     */
    private double AdjustAngle(double angle)
    {
        angle = Utilities.wrapToRange(angle + 270 - AngleOrienation, 0, 360);
        return angle;
    }

    /**
     * invoke updating the actual values and the motor outputs called
     * automatically from setDesired()
     * 
     * @return Actual vector reading of wheel
     */
    private SwerveVector updateTask()
    {
        boolean reverse = updateAngleMotor(WheelDesired.getAngle(),
                WheelDesired.getMag());

        updateShifter();

        updateDriveMotor(reverse);

        // SmartDashboard.putNumber(Name + ".desired.mag",
        // WheelDesired.getMag());
        // SmartDashboard.putNumber(Name + ".desired.ang",
        // WheelDesired.getAngle());

        return getActual();
    }

    /**
     * Update the angle motor based on the desired angle Called from
     * updateTask()
     * 
     * @return Whether the drive motor should run in the opposite direction
     */
    public boolean updateAngleMotor(double angle, double speed)
    {
        // Update the angle controller.
        AngleController.update(angle, AdjustAngle(getAnglePotValue()));

        // Control the wheel angle.
        if (speed > MinSpeed)
        {
            MotorAngle.set(-AngleController.getMotorSpeed() * maxRotationSpeed);
        }
        else
        {
            // Too slow, do nothing
            AngleController.resetIntegral();
            MotorAngle.set(0);
        }

        return AngleController.isReverseMotor();
    }

    /**
     * Update the drive motor based on the desired speed and whether to run in
     * reverse Called from updateTask()
     */
    private void updateDriveMotor(boolean reverse)
    {
//        double time = Timer.getFPGATimestamp();

        // Control the wheel speed.
        double driveMotorSpeed = WheelDesired.getMag();

        // Reverse the motor output if the angle controller is taking advantage
        // of rotational symmetry.
        if (reverse)
        {
            driveMotorSpeed = -driveMotorSpeed;
        }

        /*
         * // Update the velocity estimate.
         * DriveEncoderFilter.updateEstimate(DriveEncoder.getDistance(), time);
         * 
         * // Determine the max velocity. double driveEncoderMaxVelocity; if
         * (HighGear) { driveEncoderMaxVelocity =
         * SwerveConstants.DriveHighGearMaxVelocity; } else {
         * driveEncoderMaxVelocity = SwerveConstants.DriveLowGearMaxVelocity; }
         * 
         * // Scale the velocity estimate. double driveEncoderVelocityScaled =
         * DriveEncoderFilter.getVelocity() / driveEncoderMaxVelocity;
         * driveEncoderVelocityScaled =
         * Utilities.clampToRange(driveEncoderVelocityScaled, -1, 1);
         * 
         * // Update the wheel speed controller. double
         * driveMotorControllerError = driveMotorSpeed -
         * driveEncoderVelocityScaled; double driveMotorControllerOutput =
         * DriveEncoderController.update(driveMotorControllerError, time);
         */

        // Control the motor.
        double driveMotorOutput = driveMotorSpeed;// +
                                                  // driveMotorControllerOutput;

        if (Brake)
        {
            MotorDrive.set(0);
            MotorDrive.enableBrakeMode(true);
        }
        else
        {
            MotorDrive.enableBrakeMode(false);
            MotorDrive.set(driveMotorOutput);
        }

        // SmartDashboard.putNumber(Name + ".position.raw",
        // DriveEncoder.getRaw());
        // SmartDashboard.putNumber(Name + ".position.scaled",
        // DriveEncoder.getDistance());
        // SmartDashboard.putNumber(Name + ".speed.filtered",
        // DriveEncoderFilter.getVelocity());
        // SmartDashboard.putNumber(Name + ".speed.scaled",
        // driveEncoderVelocityScaled);
        // SmartDashboard.putNumber(Name + ".speed.error",
        // driveMotorControllerError);
        // SmartDashboard.putNumber(Name + ".speed.adjust",
        // driveMotorControllerOutput);
        // SmartDashboard.putNumber(Name + ".speed.motor", driveMotorOutput);
    }
}
