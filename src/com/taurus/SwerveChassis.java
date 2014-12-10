/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus;

import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Taurus Robotics
 * swerve chassis implementation
 */
public class SwerveChassis
{
    private SwerveVector RobotVelocity;     // robot velocity
    private double RobotRotation;           // robot rotational movement, -1 to 1 rad/s
    
    public double MaxAvailableVelocity = 1.0;
    
    private Gyro RobotGyro;
 
    private SwerveWheel[] Wheels;

    // PID controller stuff for robot angle/heading
    private class ChassisPIDOutput implements PIDOutput
    {
        private double output;
        
        public void pidWrite(double output)
        {
            this.output = output;
        }
        
        public double get()
        {
            return output;
        }
        
    }
    private PIDController ChassisPID;
    public double ChassisP = 1;
    public double ChassisI = 0;
    public double ChassisD = 0;
    public ChassisPIDOutput ChassisOutput;
    
    
    /**
     * sets up individual wheels and their positions relative to robot center
     */
    public SwerveChassis()
    {
        RobotVelocity = new SwerveVector(0, 0);
        RobotRotation = 0;
 
//        RobotGyro = new Gyro(SwerveConstants.GyroPin);
//        
//        ChassisPID = new PIDController(ChassisP, ChassisI, ChassisD, RobotGyro, ChassisOutput);
//        ChassisPID.setContinuous();
//        ChassisPID.setInputRange(0, 360);
//        ChassisPID.setOutputRange(-1,  1);
//        ChassisPID.enable();
        
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
     * Updates the chassis for Angle Drive
     * @param Velocity robot's velocity/movement using SwerveVector type
     * @param Heading robot's heading/angle using SwerveVector type
     * @return Array of SwerveVectors of the actual readings from the wheels
     */
    public SwerveVector[] UpdateAngleDrive(SwerveVector Velocity, double Heading)
    {
        double Rotation = 0;

        Velocity.setAngle(adjustAngleFromGyro(Velocity.getAngle()));
        
        // set the rotation using a PID based on current robot heading and new desired heading
        ChassisPID.setPID(ChassisP, ChassisI, ChassisD);
        ChassisPID.setSetpoint(Heading);
        
        // convert the PID output to the angular rate of rotation
        //TODO does either one make sense?
        Rotation = ChassisOutput.get();
        //Rotation = ChassisPID.get();
        
        return setWheelVectors(Velocity, Rotation);
    }
    
    /** 
     * Updates the chassis for Halo Drive from individual x and y values of velocity
     * @param VelocityX robot's velocity in the x direction, -1 to 1
     * @param VelocityY robot's velocity in the y direction, -1 to 1
     * @param Rotation robot's rotational movement, -1 to 1 rad/s
     * @return Array of SwerveVectors of the actual readings from the wheels
     */
    public SwerveVector[] UpdateHaloDrive(double VelocityX, double VelocityY, double Rotation)
    {
        return UpdateHaloDrive(new SwerveVector(VelocityX, VelocityY), Rotation);
    }
 
    /**
     * Updates the chassis for Halo Drive from SwerveVector type of velocity
     * @param Velocity robot's velocity using SwerveVector type
     * @param Rotation robot's rotational movement, -1 to 1 rad/s
     * @return Array of SwerveVectors of the actual readings from the wheels
     */
    public SwerveVector[] UpdateHaloDrive(SwerveVector Velocity, double Rotation)
    {
        Velocity.setAngle(adjustAngleFromGyro(Velocity.getAngle()));
        
        return setWheelVectors(Velocity, Rotation);
    }
    
    /**
     * Scale the wheel vectors based on max available velocity, adjust for
     * rotation rate, then set/update the desired vectors individual wheels
     * @param NewVelocity robot's velocity using SwerveVector type
     * @param NewRotation robot's rotational movement, -1 to 1 rad/s
     * @return Array of SwerveVectors of the actual readings from the wheels
     */
    private SwerveVector[] setWheelVectors(SwerveVector NewVelocity, double NewRotation)
    {
        SwerveVector[] WheelsUnscaled = new SwerveVector[SwerveConstants.WheelCount]; //Unscaled Wheel Velocities
        SwerveVector[] WheelsScaled = new SwerveVector[SwerveConstants.WheelCount];   //Scaled Wheel Velocities
        SwerveVector[] WheelsActual = new SwerveVector[SwerveConstants.WheelCount];   //Actual Wheel Velocities
        
        double MaxWantedVeloc = 0;

        // set the class variables for the velocity and rotation
        // set limitations on speed
        if (NewVelocity.getMag() < -1.0)
        {
            NewVelocity.setMag(-1.0);
        }
        else if (NewVelocity.getMag() > 1.0)
        {
            NewVelocity.setMag(1.0);
        }
        
        // set limitations on rotation
        if (NewRotation < -1.0)
        {
            NewRotation = -1.0;
        }
        else if (NewRotation > 1.0)
        {
            NewRotation = 1.0;
        }
        
        RobotVelocity = NewVelocity;
        RobotRotation = NewRotation; //Limit rotation speed
        
        // calculate vectors for each wheel
        for(int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            //calculate
            WheelsUnscaled[i] = new SwerveVector(RobotVelocity.getX() - RobotRotation * Wheels[i].getPosition().getY(),
                                                 RobotVelocity.getY() + RobotRotation * Wheels[i].getPosition().getX());

            if(WheelsUnscaled[i].getMag() >= MaxWantedVeloc)
            {
                MaxWantedVeloc = WheelsUnscaled[i].getMag();
            }
        }

        // Allow for values below maximum velocity
        for(int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            //scale values for each wheel
            WheelsScaled[i] = new SwerveVector(WheelsUnscaled[i].getMag() * (MaxAvailableVelocity / MaxWantedVeloc),
                                               WheelsUnscaled[i].getAngle(),
                                               true);

            //then set it
            WheelsActual[i] = Wheels[i].setDesired(WheelsScaled[i]);
        }
        
        return WheelsActual;
    }
    
    /**
     * Adjust the new angle based on the Gyroscope angle
     * @param Angle new desired angle
     * @return adjusted angle
     */
    private double adjustAngleFromGyro(double Angle)
    {
        double AdjustedAngle = 0;
        
        // adjust the desired angle based on the robot's current angle
        AdjustedAngle = Angle - RobotGyro.getAngle();
        
        if (AdjustedAngle > 180.0)
        {
            AdjustedAngle -= 360;
        }
        
        return AdjustedAngle;
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
    public SwerveVector getWheelActual(int index)
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
    
    /**
     * Get the SwerveWheel object for the specified index
     * @param index of wheel to get
     * @return SwerveWheel object
     */
    public SwerveWheel getWheel(int index)
    {
        return Wheels[index];
    }
}
