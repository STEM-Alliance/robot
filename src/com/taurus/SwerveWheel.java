/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Victor;

/**
 *
 * @author Taurus Robotics
 * Handle motor outputs and feedback for an individual wheel
 */
public class SwerveWheel
{
    // PID values
    public static double AngleP = 1;
    public static double AngleI = 0;
    public static double AngleD = 0;
    public static double DriveP = 1;
    public static double DriveI = 0;
    public static double DriveD = 0;
    
    // wheel stuff
    private SwerveVector WheelPosition;     // wheel location from center of robot
    private double WheelOrientation;
    private SwerveVector WheelDesired;      // wheel speed, x and y vals, hypotenuse val, angle
    private SwerveVector WheelActual;       // wheel speed, x and y vals, hypotenuse val, angle
    private Victor MotorDrive;
    private Victor MotorAngle;
    
    // encoder calculation
    private double DriveWheelDiameter = 4.0; 
    private int DriveEncoderPulses = 64;
    private double DriveEncoderRate = Math.PI*DriveWheelDiameter/DriveEncoderPulses;
    
    // potentiometer calculation
    private double AnglePotDegrees = 360; // pot has 360 degrees of range
    private double AnglePotVoltage = 5;   // pot uses 5V supply 
    private double AnglePotScale = AnglePotDegrees / AnglePotVoltage;
    
    // PID 
    private AnalogPotentiometer AnglePot;
    public SwervePIDController AnglePID;
    
    private Encoder DriveEncoder;
    public SwervePIDController DrivePID;
    
    // shifter
    private Servo Shifter;
    private double ShifterLevelHigh = 30;
    private double ShifterLevelLow = 0;
    public static int Gear;
    
    /**
     * Set up the wheel with the specific IO and orientation on the robot
     * @param Position Wheel position relative to robot center as array
     * @param Orientation Angle of wheel relative to robot 0 angle in degrees
     * @param EncoderPins Pins for Speed Encoder input as array
     * @param PotPin Pin for Angle Potentiometer
     * @param DrivePin Pin for drive motor controller
     * @param AnglePin Pin for angle motor controller
     * @param ShiftPin Pin for servo shifting
     */
    public SwerveWheel( double[] Position,
                        double Orientation,
                        int[] EncoderPins,
                        int PotPin,
                        int DrivePin,
                        int AnglePin,
                        int ShiftPin)
    {
        WheelPosition = new SwerveVector(Position);
        WheelOrientation = Orientation;
        WheelActual = new SwerveVector(0, 0);
        WheelDesired = new SwerveVector(0, 0);
        MotorDrive = new Victor(DrivePin);
        MotorAngle = new Victor(AnglePin);
        
        Shifter = new Servo(ShiftPin);
        
        DriveEncoder = new Encoder(EncoderPins[0], EncoderPins[1]);
        AnglePot = new AnalogPotentiometer(PotPin, AnglePotScale, WheelOrientation); //TODO Need offset? Maybe orientation?
        
        DriveEncoder.setDistancePerPulse(DriveEncoderRate);
        
        AnglePID = new SwervePIDController(AngleP, AngleI, AngleD, AnglePot, MotorAngle);
        AnglePID.setContinuous();
        AnglePID.setInputRange(0, 360);
        //Maybe set outputRange Depending on Victors
        AnglePID.setOutputRange(-1, 1);
        AnglePID.enable();
        
        DrivePID = new SwervePIDController(DriveP, DriveI, DriveD, DriveEncoder, MotorDrive);
        DrivePID.setInputRange(-1, 1);
        //Maybe set output range depending on stuff
        AnglePID.setOutputRange(-1, 1);
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
     * Set the shifting gear
     * @param Gear gear to use
     */
    public static void setGear(int NewGear)
    {
        Gear = NewGear;
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
        double shortestAngle = WheelDesired.getAngle();
        double motorSpeed = WheelDesired.getMag();
        
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
        
        // switch to the desired gear
        switch (Gear)
        {
            case SwerveConstants.GearLow:
                Shifter.setAngle(ShifterLevelLow);
                break;
                
            case SwerveConstants.GearHigh:
                Shifter.setAngle(ShifterLevelHigh);
                break;
                
            default:
                Shifter.setAngle(ShifterLevelLow);
                break;
        }
        
        // set the angle we want the wheel to face
        AnglePID.setPID(AngleP, AngleI, AngleD);
        AnglePID.setSetpoint(shortestAngle);
        //TODO may not need this, see line 78, AnglePot initialization
        //AnglePID.setSetpoint(((shortestAngle + 360) - WheelOrientation) % 360);

        // set the speed we want the wheel to go
        DrivePID.setPID(DriveP, DriveI, DriveD);
        DrivePID.setSetpoint(motorSpeed);
    }
}
