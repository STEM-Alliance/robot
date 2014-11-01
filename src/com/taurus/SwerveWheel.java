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
    private SwerveVector WheelPosition;     // wheel location from center of robot
    private SwerveVector WheelDesired;     // wheel speed, x and y vals, hypotenuse val, angle
    private SwerveVector WheelActual;     // wheel speed, x and y vals, hypotenuse val, angle
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

        WheelActual.setMagAngle(DrivePID.get(),AnglePID.get());
        return WheelActual;
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
        WheelActual.setMagAngle(DrivePID.get(),AnglePID.get());
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

        // handle motor outputs relative to the new readings
        // PID control here
        AnglePID.setPID(AngleP, AngleI, AngleD);
        AnglePID.setSetpoint(WheelDesired.A());
        
        DrivePID.setPID(DriveP, DriveI, DriveD);
        DrivePID.setSetpoint(WheelDesired.M());
        
    }
}
