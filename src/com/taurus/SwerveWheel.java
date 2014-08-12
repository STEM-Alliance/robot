/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus;
import edu.wpi.first.wpilibj.Victor;
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
 
        //TODO handle motor outputs relative to the new readings
        // PID control here?
 
        return Arduino.Get();
    }
}
