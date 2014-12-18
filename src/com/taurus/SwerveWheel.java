/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus;

import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Encoder;
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

    // encoder calculation
    private static final double DriveWheelDiameter = 4.0;  // inches
    private static final int DriveEncoderPulses = 64;
    private static final double DriveWheelCircumference = Math.PI * DriveWheelDiameter;
    private static final double DriveEncoderRate = DriveWheelCircumference / DriveEncoderPulses;
    
    // potentiometer calculation
    private static final double PotentiometerMax = 4.6;
    private static final double PotentiometerScale = 360 / PotentiometerMax; 
    private static final double PotentiometerOffset = -180;
    
    // deadband
    private static final double MinSpeed = 0.1;

    /**
     * Set up the wheel with the specific IO and orientation on the robot
     * @param Name Name of the wheel for display purposes
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

        //TODO the PotentiometerOffset will likely be different for every module
        // so maybe use Orientation instead
        AnglePot = new AnalogPotentiometer(PotPin, PotentiometerScale, PotentiometerOffset);
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
        SmartDashboard.putNumber(Name + ".desired.mag", WheelDesired.getMag());
        SmartDashboard.putNumber(Name + ".desired.ang", WheelDesired.getAngle());

        AngleController.update(WheelDesired.getAngle(), AnglePot.get());

        // reverse the motor output if it is the shorter path
        MotorDrive.set(AngleController.isReverseMotor() 
                ? -WheelDesired.getMag()
                : WheelDesired.getMag());
        
        if (WheelDesired.getMag() > MinSpeed)
        {
            MotorAngle.set(-AngleController.getMotorSpeed());
        }
        else
        {
            AngleController.resetIntegral();
            MotorAngle.set(0);
        }
        
        return getActual();
    }
}
