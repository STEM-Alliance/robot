/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Handle motor outputs and feedback for an individual wheel
 * @author Team 4818 Taurus Robotics
 */
public class SwerveWheel
{
    String Name;

    // wheel stuff
    private SwerveVector WheelPosition; // wheel location from center of robot
    private SwerveVector WheelDesired; // wheel speed, x and y vals, hypotenuse
                                       // val, angle
    private SwerveVector WheelActual; // wheel speed, x and y vals, hypotenuse
                                      // val, angle
    
    // motor
    private Victor MotorDrive;
    private Victor MotorAngle;

    // sensor
    private AnalogPotentiometer AnglePot;
    private Encoder DriveEncoder;

    // controller
    private SwerveAngleController AngleController;
    private PositionVelocityFilter DriveEncoderFilter;
    private PIController DriveEncoderController;
    
    private static final double P = 0.5;
    private static final double I = 0;

    // encoder calculation
    private static final double DriveWheelDiameter = 4.0;  // inches
    private static final int DriveEncoderPulses = 64;
    private static final double DriveWheelCircumference = Math.PI * DriveWheelDiameter;
    private static final double DriveEncoderRate = DriveWheelCircumference / DriveEncoderPulses / 3;
    private static final int DriveEncoderSample = 4;
    
    // encoder filter values
    // The maximum distance the encoder value may be from the true value.
    private static final double DriveEncoderMaxError = DriveEncoderRate / 2;
    // Seconds to assume the wheel is still moving even if the encoder value doesn't change. 
    private static final double DriveEncoderVelocityHoldTime = 0.001;
    
    // potentiometer calculation
    private static final double PotentiometerMax = 4.6;
    private static final double PotentiometerScale = 360 / PotentiometerMax; 
    
    // deadband
    private static final double MinSpeed = 0.1;

    /**
     * Set up the wheel with the specific IO and orientation on the robot
     * @param name Name of the wheel for display purposes
     * @param Position Wheel position relative to robot center as array
     * @param Orientation Angle of wheel relative to robot 0 angle in degrees
     * @param EncoderPins Pins for Speed Encoder input as array
     * @param PotPin Pin for Angle Potentiometer
     * @param DrivePin Pin for drive motor controller
     * @param AnglePin Pin for angle motor controller
     */
    public SwerveWheel(String name, double[] Position, double Orientation, int[] EncoderPins,
            int PotPin, int DrivePin, int AnglePin)
    {
        Name = name;

        WheelPosition = new SwerveVector(Position);
        WheelActual = new SwerveVector(0, 0);
        WheelDesired = new SwerveVector(0, 0);
        MotorDrive = new Victor(DrivePin);
        MotorAngle = new Victor(AnglePin);

        DriveEncoder = new Encoder(EncoderPins[0], EncoderPins[1]);
        DriveEncoder.setDistancePerPulse(DriveEncoderRate);
        DriveEncoder.setSamplesToAverage(DriveEncoderSample);
        DriveEncoder.start();
        
        DriveEncoderFilter = new PositionVelocityFilter(DriveEncoderMaxError, DriveEncoderVelocityHoldTime);
        DriveEncoderController = new PIController(P, I, 1.0);

        AnglePot = new AnalogPotentiometer(PotPin, PotentiometerScale, Orientation);
        AngleController = new SwerveAngleController(name + ".ctl");
    }

    /** 
     * Set the desired wheel vector, auto updates the PID controllers
     * @param NewDesired
     * @return Actual vector reading of wheel
     */
    public SwerveVector setDesired(SwerveVector NewDesired)
    {
        WheelDesired = NewDesired;

        return updateTask();
    }

    /** 
     * Get the desired vector (velocity and rotation) of this wheel instance
     * @return Desired vector
     */
    public SwerveVector getDesired()
    {
        return WheelDesired;
    }

    /** 
     * Get the actual vector reading of the wheel
     * @return Actual vector reading of wheel
     */
    public SwerveVector getActual()
    {
        WheelActual.setMagAngle(DriveEncoder.getRate(), AnglePot.get());
        return WheelActual;
    }

    /** 
     * Get the wheel's position relative to robot center
     * @return Wheel position
     */
    public SwerveVector getPosition()
    {
        return WheelPosition;
    }

    /** 
     * invoke updating the actual values and the motor outputs
     * called automatically from setDesired()
     * @return Actual vector reading of wheel
     */
    private SwerveVector updateTask()
    {
        double time = Timer.getFPGATimestamp();

        // Update the angle controller.
        AngleController.update(WheelDesired.getAngle(), AnglePot.get());
        
        // Control the wheel angle.
        if (WheelDesired.getMag() > MinSpeed)
        {
            MotorAngle.set(-AngleController.getMotorSpeed());
        }
        else
        {
            // Too slow, do nothing
            AngleController.resetIntegral();
            MotorAngle.set(0);
        }
        
        // Control the wheel speed.
        double driveMotorSpeed = WheelDesired.getMag();
        
        // Reverse the motor output if the angle controller is taking advantage of rotational symmetry.
        if (AngleController.isReverseMotor())
            driveMotorSpeed = -driveMotorSpeed;
        
        // Filter the position and get a velocity estimate.
        DriveEncoderFilter.updateEstimate(DriveEncoder.getDistance(), time);
        
        // Update the wheel speed controller.
        // TODO: driveEncoderMaxVelocity = DriveWheelCircumference * ??? (depends on gear ratio)
        double driveEncoderMaxVelocity = DriveWheelCircumference * 2.0;
        double driveMotorControllerOutput = DriveEncoderController.update(driveMotorSpeed - DriveEncoderFilter.getVelocity() / driveEncoderMaxVelocity, time);
        
        // Control the motor.
        double driveMotorOutput = driveMotorSpeed + driveMotorControllerOutput;
        
        MotorDrive.set(driveMotorOutput);
        //MotorDrive.set(driveMotorSpeed);

        SmartDashboard.putNumber(Name + ".desired.mag", WheelDesired.getMag());
        SmartDashboard.putNumber(Name + ".desired.ang", WheelDesired.getAngle());
        SmartDashboard.putNumber(Name + ".position.raw", DriveEncoder.getRaw());
        SmartDashboard.putNumber(Name + ".position.scaled", DriveEncoder.getDistance());
        SmartDashboard.putNumber(Name + ".position.filtered", DriveEncoderFilter.getPosition());
        SmartDashboard.putNumber(Name + ".speed.filtered", DriveEncoderFilter.getVelocity());
        SmartDashboard.putNumber(Name + ".speed.adjust", driveMotorControllerOutput);
        SmartDashboard.putNumber(Name + ".speed.motor", driveMotorOutput);
        
        return getActual();
    }
}
