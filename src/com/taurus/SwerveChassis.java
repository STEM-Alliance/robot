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
 
    /**
     * sets up individual wheels and their positions relative to robot center
     */
    public SwerveChassis()
    {
        RobotVelocity = new SwerveVector(0, 0);
        RobotRotation = 0;
 
        RobotGyro = new Gyro(5);
         
        Wheels = new SwerveWheel[4];
 
        // {x, y}, {EncoderA, EncoderB}, Drive, Angle, Pot
        Wheels[0] = new SwerveWheel(new double[]{-1,  1}, new int[]{1, 2}, 1, 1, 2); // Left Front
        Wheels[1] = new SwerveWheel(new double[]{ 1,  1}, new int[]{3, 4}, 2, 3, 4); // Right Front
        Wheels[2] = new SwerveWheel(new double[]{ 1, -1}, new int[]{5, 6}, 3, 5, 6); // Right Back
        Wheels[3] = new SwerveWheel(new double[]{-1, -1}, new int[]{7, 8}, 4, 7, 8); // Left Back
 
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
        SwerveVector[] WheelsUnscaled = new SwerveVector[4]; //Unscaled Wheel Velocities
        SwerveVector[] WheelsScaled = new SwerveVector[4];   //Scaled Wheel Velocities
        SwerveVector[] WheelsActual = new SwerveVector[4];   //Actual Wheel Velocities
        double VelocScale = 1;
        double MaxWantedVeloc = 0;

        RobotVelocity = Velocity;
        RobotRotation = Rotation; //Limit rotation speed
       
        //TODO get gyro
        RobotGyro.getAngle();

        for(int i = 0; i < 4; i++)
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

        for(int i = 0; i < 4; i++)
        {
            //set values for each wheel
            WheelsScaled[i] = new SwerveVector(VelocScale * WheelsUnscaled[i].M(),
                                               WheelsUnscaled[i].A(),
                                               true);

            Wheels[i].setDesired(WheelsScaled[i]);

            WheelsActual[i] = GetActual(i);
        }
        
        return WheelsActual;
    }

    /**
     * Get the actual reading of a wheel
     * @param Index Index of the wheel
     * @return Actual reading of the wheel
     */
    public SwerveVector GetActual(int Index)
    {
        return Wheels[Index].getActual();
    }  
}
