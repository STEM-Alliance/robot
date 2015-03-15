/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus.swerve;

import com.taurus.MagnetoPot;
import com.taurus.Utilities;
import com.taurus.controller.Controller;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Talon;

/**
 * Handle motor outputs and feedback for an individual wheel
 * 
 * @author Team 4818 Taurus Robotics
 */
public class SwerveWheel {
    String Name;
    int Number;

    // wheel stuff
    private SwerveVector WheelPosition; // wheel location from center of robot
    private SwerveVector WheelDesired; // wheel speed, x and y vals, hypotenuse
                                       // val, angle
    private SwerveVector WheelActual; // wheel speed, x and y vals, hypotenuse
                                      // val, angle

//    private Servo Shifter;
//    private int ShifterValueHigh;
//    private int ShifterValueLow;
//    private boolean HighGear;

    private boolean Brake;

    private double maxRotationSpeed = .75;

    private double AngleOrientation = 0;

    // motor
    private CANTalon MotorDrive;
    public Talon MotorAngle;

    // sensor
    public MagnetoPot AnglePot;
    //private Encoder DriveEncoder;
    
    public DigitalInput CalibrationSensor;
    
    /**
     * angle that the sensor is mounted compared to 0
     */
    public double CalibrationSensorAngle;
    private final double CALIBRATION_MINIMUM = 20;

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

    private final Controller controller;
    
    /**
     * Set up the wheel with the specific IO and orientation on the robot
     * 
     * @param Number
     *            Number of the wheel
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
    public SwerveWheel(int Number, double[] Position, double Orientation,
            /*int[] EncoderPins, */int PotPin, int DriveAddress, int AnglePin,
            /*int ShiftPin, int[] ShiftVals*/
            int AngleCalibrationPin, Controller controller)
    {
        Name = "Wheel" + this.Number;
        this.Number = Number;

        WheelPosition = new SwerveVector(Position);
        WheelActual = new SwerveVector(0, 0);
        WheelDesired = new SwerveVector(0, 0);
        MotorDrive = new CANTalon(DriveAddress);
        MotorAngle = new Talon(AnglePin);

//        HighGear = true;
//        Shifter = new Servo(ShiftPin);
//        ShifterValueHigh = ShiftVals[0];
//        ShifterValueLow = ShiftVals[1];

        //DriveEncoder = new Encoder(EncoderPins[0], EncoderPins[1]);
        //DriveEncoder.setDistancePerPulse(SwerveConstants.DriveEncoderRate);

//        DriveEncoderFilter = new VelocityCalculator();
//        DriveEncoderController = new PIController(DriveP, DriveI, 1.0);

        // AnglePot = new AnalogPotentiometer(PotPin, 360 + Math.abs(SpinMin) +
        // Math.abs(SpinMax), -SpinMin);
        AnglePot = new MagnetoPot(PotPin, 360);
        AngleController = new SwerveAngleController(Name + ".ctl");

        AngleOrientation = Orientation;
        
        //CalibrationSensor = new DigitalInput(AngleCalibrationPin);
        
        this.controller = controller;
    }

    /**
     * Set the desired wheel vector, auto updates the PID controllers
     * 
     * @param NewDesired
     * @param HighGear
     * @return Actual vector reading of wheel
     */
    public SwerveVector setDesired(SwerveVector NewDesired,
           /* boolean NewHighGear,*/ boolean NewBrake)
    {
        WheelDesired = NewDesired;
//        HighGear = NewHighGear;
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
    
    /**
     * Get whether the wheel is in high gear or low gear
     * 
     * @return Whether the wheel is in high gear
     */
//    public boolean getIsHighGear()
//    {
//        return HighGear;
//    }

//    private void updateShifter()
//    {
//        if (HighGear)
//        {
//            Shifter.setAngle(ShifterValueHigh);
//
//        }
//        else
//        {
//            Shifter.setAngle(ShifterValueLow);
//        }
//    }

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
     * Adjust the angle for the orientation value.
     * Will auto calibrate if the calibration sensor is triggered
     * 
     * @param angle
     * @return adjusted angle from orientation value
     */
    private double AdjustAngle(double angle)
    {
        this.AngleOrientation = Application.prefs.getDouble("Wheel_Orientation_" + Number, AngleOrientation);

        double AdjustedAngle = Utilities.wrapToRange(angle + 270 - AngleOrientation, 0, 360);
        
        
        if(controller.getWheelCal())
        {
//            if(CalibrationSensor.get())
//            {
//                // the Calibration Sensor is triggered, so we should be facing forward
//                if(Math.abs(Utilities.wrapToRange(AdjustedAngle, -180, 180)) > CALIBRATION_MINIMUM)
//                {
//                    // we're more than CALIBRATION_MINIMUM away, yet the sensor is triggered,
//                    // we need to then update the angle
//                    this.AngleOrientation = Utilities.wrapToRange(270 - angle, 0, 360);
//                    
//                    Application.prefs.putDouble("Wheel_Orientation_" + Number, AngleOrientation);
//                    
//                    AdjustedAngle = Utilities.wrapToRange(angle + 270 - AngleOrientation, 0, 360);
//                }
//                
//            }
        }
        
        return AdjustedAngle;
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

//        updateShifter();

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
