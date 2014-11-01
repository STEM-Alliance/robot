/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus;

import edu.wpi.first.wpilibj.Gyro;

/**
 *
 * @author Taurus Robotics
 * swerve chassis implementation
 */
public class SwerveChassis
{
    private SwerveVector RobotVelocity;     // robot velocity
    private double RobotRotation;    // robot rotational movement, -1 to 1 rad/s
    
    private double MaxAvailableVelocity = 30;
    
    private Gyro RobotGyro;
 
    private SwerveWheel[] Wheels;
 
    private static final int WheelCount = 4;
    
    /**
     * sets up individual wheels and their positions relative to robot center
     */
    public SwerveChassis()
    {
        RobotVelocity = new SwerveVector(0, 0);
        RobotRotation = 0;
 
        RobotGyro = new Gyro(SwerveConstants.GyroPin);
        
        Wheels = new SwerveWheel[WheelCount];
 
        // {x, y}, {EncoderA, EncoderB}, Pot, Drive, Angle
        for(int i = 0; i < WheelCount; i++)
        {
            Wheels[i] = new SwerveWheel(SwerveConstants.WheelPositions[i],
                                        SwerveConstants.WheelEncoderPins[i],
                                        SwerveConstants.WheelPotPins[i],
                                        SwerveConstants.WheelDriveMotorPins[i],
                                        SwerveConstants.WheelAngleMotorPins[i]);
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
        SwerveVector[] WheelsUnscaled = new SwerveVector[WheelCount]; //Unscaled Wheel Velocities
        SwerveVector[] WheelsScaled = new SwerveVector[WheelCount];   //Scaled Wheel Velocities
        SwerveVector[] WheelsActual = new SwerveVector[WheelCount];   //Actual Wheel Velocities
        
        double VelocScale = 1;
        double MaxWantedVeloc = 0;

        RobotVelocity = Velocity;
        RobotRotation = Rotation; //Limit rotation speed
       
        //TODO get gyro
        RobotGyro.getAngle();
        
        //TODO adjust RobotRotation based on RobotGyro.getAngle()
        

        for(int i = 0; i < WheelCount; i++)
        {
            //calculate and scale
            WheelsUnscaled[i] = new SwerveVector(RobotVelocity.X() - RobotRotation * Wheels[i].getPosition().Y(),
                                                 RobotVelocity.Y() + RobotRotation * Wheels[i].getPosition().X());

            if(WheelsUnscaled[i].M() >= MaxWantedVeloc)
            {
                MaxWantedVeloc = WheelsUnscaled[i].M();
            }
        }

        VelocScale = MaxAvailableVelocity / MaxWantedVeloc;

        for(int i = 0; i < WheelCount; i++)
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
}
