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
 * Handle motor outputs and feedback for an individual wheel
 */
public class SwerveWheel
{
    public double AngleP = 1;
    public double AngleI = 0;
    public double AngleD = 0;
    public double DriveP = 1;
    public double DriveI = 0;
    public double DriveD = 0;
    
    private SwerveVector WheelPosition;     // wheel location from center of robot
    private SwerveVector WheelDesired;      // wheel speed, x and y vals, hypotenuse val, angle
    private SwerveVector WheelActual;       // wheel speed, x and y vals, hypotenuse val, angle
    private Victor MotorDrive;
    private Victor MotorAngle;
    
    private double DriveWheelDiameter = 4.0; 
    private int DriveEncoderPulses = 64;
    private double DriveEncoderRate = Math.PI*DriveWheelDiameter/DriveEncoderPulses;
    
    private AnalogPotentiometer AnglePot;
    private PIDController AnglePID;
    
    private Encoder DriveEncoder;
    private PIDController DrivePID;
    
    /**
     * 
     * @param Position Wheel position relative to robot center as array
     * @param Encoder Speed Encoder input pins as array
     * @param Pot Angle Potentiometer pin
     * @param Drive Pin for drive motor controller
     * @param Angle Pin for angle motor controller
     */
    public SwerveWheel(double[] Position, int[] Encoder, int Pot, int Drive, int Angle)
    {
        WheelPosition = new SwerveVector(Position);
        WheelActual = new SwerveVector(0, 0);
        WheelDesired = new SwerveVector(0, 0);
        MotorDrive = new Victor(Drive);
        MotorAngle = new Victor(Angle);
        
        DriveEncoder = new Encoder(Encoder[0], Encoder[1]);
        AnglePot = new AnalogPotentiometer(Pot);
        
        DriveEncoder.setDistancePerPulse(DriveEncoderRate);
       
        
        AnglePID = new PIDController(AngleP, AngleI, AngleD, AnglePot, MotorAngle);
        AnglePID.setContinuous();
        AnglePID.setInputRange(0, 360);
        //Maybe set outputRange Depending on Victors 
        AnglePID.enable();
        
        DrivePID = new PIDController(DriveP, DriveI, DriveD, DriveEncoder, MotorDrive);
        DrivePID.setInputRange(-1, 1);
        //Maybe set output range depending on stuff
        DrivePID.enable();
    }
 
    /** 
     * Set the desired wheel vector, auto updates the PID controllers
     * @param NewDesired
     * @return Actual vector reading of wheel
     */
    public SwerveVector setDesired(SwerveVector NewDesired){
        WheelDesired = NewDesired;
        
        UpdateTask();

        return getActual();
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
    	//TODO is this right?
        WheelActual.setMagAngle(DrivePID.get(), AnglePID.get());
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
     */
    private void UpdateTask()
    {        
        // If the desired angle is 90 < desired angle < 270, 
        // use the angle (desired angle + 180)mod(360) and reverse
        // the motors
        // Else use the desired angle and do not reverse the motors
        //
        // Desired angle, Used angle, reverse motors
        // 0<x<90, x, n
        // 90<x<270, (x + 180)mod(360), y
        // 270<x<360, x, n
        
        // TODO - Make shortestAngle account for actual angle
        
        // The angle we tell the PID to move to, move wheel the least
        double shortestAngle = WheelDesired.A();
        double motorSpeed = WheelDesired.M();
        
        if (shortestAngle > 90 && shortestAngle < 270)
        {
            // (angle + 180)mod(360)
            shortestAngle = (shortestAngle + 180) % 360;
            motorSpeed = motorSpeed * -1;
        }
        else
        {
            // Do nothing
        }
        
        // Tell the software that controls the wheels what
        // angle we want the wheel to face
        AnglePID.setPID(AngleP, AngleI, AngleD);
        AnglePID.setSetpoint(shortestAngle);

        // Tell the software that controls the wheels what
        // speed we want the wheel to go
        DrivePID.setPID(DriveP, DriveI, DriveD);
        DrivePID.setSetpoint(motorSpeed);        
    }
}
