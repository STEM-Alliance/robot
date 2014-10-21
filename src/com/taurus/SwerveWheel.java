/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.PIDController;

/**
 *
 * @author Taurus Robotics
 handle motor outputs and feedback for an individual wheel
 */
public class SwerveWheel
{
    public SwervePoint WheelPosition;     // wheel location from center of robot
    private SwervePoint WheelDesired;     // wheel speed, x and y vals, hypotenuse val, angle
    private SwervePoint WheelActual;     // wheel speed, x and y vals, hypotenuse val, angle
    private Victor MotorDrive;
    private Victor MotorAngle;
    
    public double AngleP = 1;
    public double AngleI = 0;
    public double AngleD = 0;
    public double DriveP = 1;
    public double DriveI = 0;
    public double DriveD = 0;
    
    private double DriveWheelDiameter = 4.0; 
    private int DriveEncoderPulses = 64;
    private double DriveEncoderRate = Math.PI*DriveWheelDiameter/DriveEncoderPulses;
    
    //private SwervePIDSource AngleSource;
    private AnalogPotentiometer AnglePot;
    private PIDController AnglePID;
    
    //private SwervePIDSource DriveSource;
    private Encoder DriveEncoder;
    private PIDController DrivePID;
    // constructor
    // x and y are location relative to robot center
    // Address is slave address of arduino
    public SwerveWheel(double x, double y, int EncoderA, int EncoderB, int Drive, int Angle, int Pot)
    {
        WheelPosition = new SwervePoint(x, y);
        WheelActual = new SwervePoint(0, 0);
        WheelDesired = new SwervePoint(0, 0);
        MotorDrive = new Victor(Drive);
        MotorAngle = new Victor(Angle);
        
        DriveEncoder = new Encoder(EncoderA, EncoderB);
        AnglePot = new AnalogPotentiometer(Pot);
        
        DriveEncoder.setDistancePerPulse(DriveEncoderRate);
       
        
        AnglePID = new PIDController(AngleP, AngleI, AngleD, AnglePot, MotorAngle);
        AnglePID.setContinuous();
        AnglePID.setInputRange(0, 360);
        //Maybe set outputRange Depending on Victors 
        AnglePID.enable();
        
        DrivePID = new PIDController(DriveP, DriveI, DriveD, DriveEncoder, MotorDrive);
        DrivePID.setInputRange(-1, 1);
        //MAybe set output range depending on stuff
        DrivePID.enable();
    }
 
    // set the velocity and desired rotation of the wheel using the whole robot's desired values
    // auto calculates what is needed for this specific wheel instance
    // return: actual reading from wheel
    public void Set(SwervePoint RobotVelocity, double RobotRotation)
    {
        WheelDesired = new SwervePoint(RobotVelocity.X() - RobotRotation * WheelPosition.Y(),
                                        RobotVelocity.Y() + RobotRotation * WheelPosition.X());
 
        UpdateTask();
    }
    
    public void SetDesired(SwervePoint NewDesired){
        WheelDesired = NewDesired;
        
        UpdateTask();
    }
 
    // get the desired/requested velocity and rotation of this wheel instance
    public SwervePoint GetDesired()
    {
        return WheelDesired;
    }
 
    public SwervePoint GetActual()
    {
        return WheelActual;
    }

    
    // Manually invoke updating the actual values and the motor outputs
    // called automatically from Set()
    // return: actual reading from wheel
    public void UpdateTask()
    {

        // handle motor outputs relative to the new readings
        // PID control here
        AnglePID.setPID(AngleP, AngleI, AngleD);
        AnglePID.setSetpoint(WheelDesired.Angle());
        
        DrivePID.setPID(DriveP, DriveI, DriveD);
        DrivePID.setSetpoint(WheelDesired.H());
        
    }
}
