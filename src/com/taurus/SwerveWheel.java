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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * @author Taurus Robotics
 * Handle motor outputs and feedback for an individual wheel
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
    private Victor MotorDrive;
    private Victor MotorAngle;

    // encoder calculation
    private double DriveWheelDiameter = 4.0;
    private int DriveEncoderPulses = 64;
    private double DriveEncoderRate = Math.PI * DriveWheelDiameter / DriveEncoderPulses;

    // sensor
    private SwerveAngleReader AnglePot;
    private Encoder DriveEncoder;

    // controller
    private SwerveAngleController AngleController;

    // shifter
    // private Servo Shifter;
    // private double ShifterLevelHigh = 30;
    // private double ShifterLevelLow = 0;
    public static int Gear;

    /**
     * Set up the wheel with the specific IO and orientation on the robot
     * @param Name Name of the wheel for display purposes
     * @param Position Wheel position relative to robot center as array
     * @param Orientation Angle of wheel relative to robot 0 angle in degrees
     * @param EncoderPins Pins for Speed Encoder input as array
     * @param PotPin Pin for Angle Potentiometer
     * @param DrivePin Pin for drive motor controller
     * @param AnglePin Pin for angle motor controller
     * @param ShiftPin Pin for servo shifting
     */
    public SwerveWheel(String name, double[] Position, double Orientation, int[] EncoderPins,
            int PotPin, int DrivePin, int AnglePin, int ShiftPin)
    {
        Name = name;

        WheelPosition = new SwerveVector(Position);
        WheelActual = new SwerveVector(0, 0);
        WheelDesired = new SwerveVector(0, 0);
        MotorDrive = new Victor(DrivePin);
        MotorAngle = new Victor(AnglePin);

        // Shifter = new Servo(ShiftPin);

        DriveEncoder = new Encoder(EncoderPins[0], EncoderPins[1]);
        DriveEncoder.setDistancePerPulse(DriveEncoderRate);

        AnglePot = new SwerveAngleReader(name + ".pot", new AnalogPotentiometer(PotPin),
                Orientation);

        AngleController = new SwerveAngleController();
    }

    /** 
     * Set the desired wheel vector, auto updates the PID controllers
     * @param NewDesired
     * @return Actual vector reading of wheel
     */
    public SwerveVector setDesired(SwerveVector NewDesired)
    {
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
        // switch to the desired gear
        switch (Gear)
        {
            case SwerveConstants.GearLow:
                // Shifter.setAngle(ShifterLevelLow);
                break;

            case SwerveConstants.GearHigh:
                // Shifter.setAngle(ShifterLevelHigh);
                break;

            default:
                // Shifter.setAngle(ShifterLevelLow);
                break;
        }

        SmartDashboard.putNumber(Name + ".desired.mag", WheelDesired.getMag());
        SmartDashboard.putNumber(Name + ".desired.ang", WheelDesired.getAngle());

        double angle = AnglePot.get();
        AngleController.Update(WheelDesired.getAngle(), angle);

        SmartDashboard.putBoolean(Name + ".ctl.rev", AngleController.isReverseDriveMotor());
        SmartDashboard.putNumber(Name + ".ctl.rot", AngleController.getAngleMotorSpeed());

        MotorDrive.set(AngleController.isReverseDriveMotor() 
                ? -WheelDesired.getMag()
                : WheelDesired.getMag());
        MotorAngle.set(AngleController.getAngleMotorSpeed());
    }
}
