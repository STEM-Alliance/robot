/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Taurus Robotics
 * swerve chassis implementation
 */
public class SwerveChassis
{
    private SwerveVector RobotVelocity;     // robot velocity
    private double RobotRotation;           // robot rotational movement, -1 to 1 rad/s
    
    public double MaxVelocity = 30;
    
    private Gyro RobotGyro;
 
    private SwerveWheel[] Wheels;
 
        
    
    /**
     * sets up individual wheels and their positions relative to robot center
     */
    public SwerveChassis()
    {
        RobotVelocity = new SwerveVector(0, 0);
        RobotRotation = 0;
 
        RobotGyro = new Gyro(SwerveConstants.GyroPin);
        
        Wheels = new SwerveWheel[SwerveConstants.WheelCount];
 
        // {x, y}, Orientation, {EncoderA, EncoderB}, Pot, Drive, Angle, Shifter
        for(int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            Wheels[i] = new SwerveWheel(SwerveConstants.WheelPositions[i],
                                        SwerveConstants.WheelOrientationAngle[i],
                                        SwerveConstants.WheelEncoderPins[i],
                                        SwerveConstants.WheelPotPins[i],
                                        SwerveConstants.WheelDriveMotorPins[i],
                                        SwerveConstants.WheelAngleMotorPins[i],
                                        SwerveConstants.WheelShiftServoPins[i]);
        }
 
    }
 
    /** 
     * Updates from individual x and y values of velocity
     * @param VelocityX robot's velocity in the x direction, -1 to 1
     * @param VelocityY robot's velocity in the y direction, -1 to 1
     * @param Rotation robot's rotational movement, -1 to 1 rad/s
     * @return Array of SwerveVectors of the actual readings from the wheels
     */
    public SwerveVector[] Update(double VelocityX, double VelocityY, double Rotation)
    {
        return Update(new SwerveVector(VelocityX, VelocityY), Rotation);
    }
 
    /**
     * Updates from SwerveVector type of velocity
     * @param Velocity robot's velocity using SwerveVector type
     * @param Rotation robot's rotational movement, -1 to 1 rad/s
     * @return Array of SwerveVectors of the actual readings from the wheels
     */
    public SwerveVector[] Update(SwerveVector Velocity, double Rotation)
    {
        SwerveVector[] WheelsUnscaled = new SwerveVector[SwerveConstants.WheelCount]; //Unscaled Wheel Velocities
        SwerveVector[] WheelsScaled = new SwerveVector[SwerveConstants.WheelCount];   //Scaled Wheel Velocities
        SwerveVector[] WheelsActual = new SwerveVector[SwerveConstants.WheelCount];   //Actual Wheel Velocities
        
        double VelocScale = 1;
        double MaxWantedVeloc = 0;
        double AdjustedAngle = 0;
        
        // adjust the desired angle based on the robot's current angle
        AdjustedAngle = Velocity.A() - RobotGyro.getAngle();
        
        if (AdjustedAngle > 180.0)
        {
            AdjustedAngle -= 360;
        }
        
        Velocity.setAngle(AdjustedAngle);
        
        // set the class variables for the velocity and rotation
        RobotVelocity = Velocity;
        RobotRotation = Rotation; //Limit rotation speed
        
        // calculate vectors for each wheel
        for(int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            //calculate
            WheelsUnscaled[i] = new SwerveVector(RobotVelocity.X() - RobotRotation * Wheels[i].getPosition().Y(),
                                                 RobotVelocity.Y() + RobotRotation * Wheels[i].getPosition().X());

            if(WheelsUnscaled[i].M() >= MaxWantedVeloc)
            {
                MaxWantedVeloc = WheelsUnscaled[i].M();
            }
        }

        // TODO - Allow for values below maximum velocity
        VelocScale = MaxVelocity / MaxWantedVeloc;

        for(int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            //scale values for each wheel
            WheelsScaled[i] = new SwerveVector(VelocScale * WheelsUnscaled[i].M(),
                                               WheelsUnscaled[i].A(),
                                               true);

            //then set it
            WheelsActual[i] = Wheels[i].setDesired(WheelsScaled[i]);
        }
        
        return WheelsActual;
    }
    
    /**
     * Set the shifting gear
     * @param GearHigh if true, shift to high gear, else low gear
     */
    public void setGearHigh(boolean GearHigh)
    {
        // Shift gears if necessary
        if(GearHigh)
        {
            SwerveWheel.setGear(SwerveConstants.GearHigh);
        }
        else
        {
            SwerveWheel.setGear(SwerveConstants.GearLow);
        }
    }
    
    /**
     * Get the shifting gear
     * @return true if currently in high gear, else false
     */
    public boolean getGearHigh()
    {
        boolean retVal = false;
        
        if(SwerveWheel.Gear == SwerveConstants.GearHigh)
        {
            retVal = true;
        }
        
        return retVal;
    }
    
    /**
     * Get the actual reading of a wheel
     * @param index Index of the wheel
     * @return Actual reading of the wheel
     */
    public SwerveVector getActual(int index)
    {
        return Wheels[index].getActual();
    }
    
    /**
     * Get the Gyro object
     * @return Gyro object
     */
    public Gyro getGyro()
    {
    	return RobotGyro;
    }
    
    public SwerveWheel getWheel(int index)
    {
        return Wheels[index];
    }
}
