/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus;

/**
 *
 * @author Taurus Robotics
 * swerve chassis implementation
 */
public class SwerveChassis
{
    private SwervePoint RobotVelocity;     // robot velocity
    private double RobotRotation;    // robot rotational movement, -1 to 1 rad/s
 
    private SwerveGyro Gyro;
 
    private SwerveWheel[] Wheels;
 
    // constructor
    // sets up individual wheels and their positions relative to robot center
    public SwerveChassis()
    {
        RobotVelocity = new SwervePoint(0, 0);
        RobotRotation = 0;
 
        Gyro = new SwerveGyro();
 
        Wheels = new SwerveWheel[4];
 
        // x, y, EncoderA, EncderB, Drive, Angle, Pot
        Wheels[0] = new SwerveWheel(-1,  1, 1, 2, 1, 2, 1); // Left Front
        Wheels[1] = new SwerveWheel( 1,  1, 3, 4, 3, 4, 2); // Right Front
        Wheels[2] = new SwerveWheel( 1, -1, 5, 6, 5, 6, 3); // Right Back
        Wheels[3] = new SwerveWheel(-1, -1, 6, 7, 7, 8, 4); // Left Back
 
    }
 
    // Updates from individual x and y values of velocity
    // VelocityX = robot's velocity in the x direction, -1 to 1
    // VelocityY = robot's velocity in the y direction, -1 to 1
    // Rotation  = robot's rotational movement, -1 to 1 rad/s
    public SwervePoint[] Update(double VelocityX, double VelocityY, double Rotation)
    {
        return Update(new SwervePoint(VelocityX, VelocityY), Rotation);
    }
 
    // Updates from SwervePoint type of velocity
    // Velocity = robot's velocity using SwervePoint type
    // Rotation = robot's rotational movement, -1 to 1 rad/s
    // return: list of SwervePoints of the actual readings from the wheels
    public SwervePoint[] Update(SwervePoint Velocity, double Rotation)
    {
        SwervePoint[] Actuals = new SwervePoint[4];
        RobotVelocity = Velocity;
        RobotRotation = Rotation;
       
        //TODO get gyro
 
        for(int i = 0; i < 4; i++)
        {
            Wheels[i].Set(RobotVelocity, RobotRotation);
 
            Actuals[i] = Wheels[i].GetActual();
        }
 
        return Actuals;
    }
 
    // get the actual reading of the wheel at index
    public SwervePoint GetActual(int Index)
    {
        return Wheels[Index].GetActual();
    }  
}
