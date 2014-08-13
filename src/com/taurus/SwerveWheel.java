/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;

/**
 *
 * @author Taurus Robotics
 * handle motor outputs and feedback for an individual wheel
 */
public class SwerveWheel
{
    private SwervePoint WheelPosition;     // wheel location from center of robot
    private SwervePoint WheelVelocity;     // wheel speed, x and y vals, hypotenuse val, angle
    private Victor MotorDrive;
    private Victor MotorAngle;
    private SwerveArduino Arduino;
    
    public double AngleP = 1;
    public double AngleI = 0;
    public double AngleD = 0;
    public double DriveP = 1;
    public double DriveI = 0;
    public double DriveD = 0;
    
    private SwervePIDSource AngleSource;
    private PIDController AnglePID;
    
    private SwervePIDSource DriveSource;
    private PIDController DrivePID;
    // constructor
    // x and y are location relative to robot center
    // Address is slave address of arduino
    public SwerveWheel(double x, double y, int Address, int Drive, int Angle)
    {
        WheelPosition = new SwervePoint(x, y);
        WheelVelocity = new SwervePoint(0, 0);
        MotorDrive = new Victor(Drive);
        MotorAngle = new Victor(Angle);
        Arduino = new SwerveArduino(Address);
        
        //TODO need to implement this
        AngleSource = new SwervePIDSource();
        DriveSource = new SwervePIDSource();
        
        AnglePID = new PIDController(AngleP, AngleI, AngleD, AngleSource, MotorAngle);
        AnglePID.setContinuous();
        AnglePID.setInputRange(0, 360);
        //Maybe set outputRange Depending on Victors 
        AnglePID.enable();
        
        DrivePID = new PIDController(DriveP, DriveI, DriveD, DriveSource, MotorDrive);
        DrivePID.setInputRange(-1, 1);
        //MAybe set output range depending on stuff
        DrivePID.enable();
    }
 
    // set the velocity and desired rotation of the wheel using the whole robot's desired values
    // auto calculates what is needed for this specific wheel instance
    // return: actual reading from wheel
    public SwervePoint Set(SwervePoint RobotVelocity, double RobotRotation)
    {
        WheelVelocity = new SwervePoint(RobotVelocity.X() - RobotRotation * WheelPosition.Y(),
                                        RobotVelocity.Y() + RobotRotation * WheelPosition.X());
 
        return UpdateTask();
    }
 
    // get the desired/requested velocity and rotation of this wheel instance
    public SwervePoint GetDesired()
    {
        return WheelVelocity;
    }
 
    // get the actual velocity and rotation of this wheel instance
    // requires the UpdateTask to be called prior
    public SwervePoint GetActual()
    {
        return Arduino.Get();
    }
 
    // Manually invoke updating the actual values and the motor outputs
    // called automatically from Set()
    // return: actual reading from wheel
    public SwervePoint UpdateTask()
    {
        Arduino.Update();
 
        // handle motor outputs relative to the new readings
        // PID control here
        AngleSource.pidSet(Arduino.Get().Angle());
        AnglePID.setPID(AngleP, AngleI, AngleD);
        AnglePID.setSetpoint(WheelVelocity.Angle());
        
        DriveSource.pidSet(Arduino.Get().H());
        DrivePID.setPID(DriveP, DriveI, DriveD);
        DrivePID.setSetpoint(WheelVelocity.H());
        
        return Arduino.Get();
    }
}
