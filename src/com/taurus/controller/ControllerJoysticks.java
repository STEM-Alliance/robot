package com.taurus.controller;

import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;

public class ControllerJoysticks implements ControllerSwerve {

    private Joystick left;
    private Joystick right;

    public static final double DEADBAND = 0.05;

    /**
     * Create a new Joysticks Controller object
     */
    public ControllerJoysticks()
    {
        left = new Joystick(0);
        right = new Joystick(1);

    }

    public double getX(Hand hand)
    {
        return GetJoy(hand).getX(hand);
    }
    public double getY(Hand hand)
    {
        return GetJoy(hand).getY(hand);
    }
    /**
     * Get the joystick object for the specified hand
     * @param hand
     * @return left or right Joystick
     */
    private Joystick GetJoy(Hand hand)
    {
        if(hand == Hand.kLeft)
        {
            return left;
        }
        else
        {
            return right;
        }
    }
    
    /**
     * Get the magnitude of the direction vector formed by the
     * joystick's current position relative to its origin
     * @param hand Hand associated with the Joystick
     * @return the magnitude of the direction vector
     */
    public double getMagnitude(Hand hand)
    {
        double value = 0;

        value = GetJoy(hand).getMagnitude();

        if(value < DEADBAND)
        {
            value = 0;
        }
        return value;
    }

    /**
     * Get the direction of the vector formed by the joystick and its origin in degrees
     * @param hand Hand associated with the Joystick
     * @return The direction of the vector in degrees
     */
    public double getDirectionDegrees(Hand hand)
    {
        return GetJoy(hand).getDirectionDegrees();
    }
    
    /**
     * Get the direction of the vector formed by the joystick and its origin in radians
     * @param hand Hand associated with the Joystick
     * @return The direction of the vector in radians
     */
    public double getDirectionRadians(Hand hand)
    {
        return GetJoy(hand).getDirectionRadians();
    }

    /**
     * Get the Rotation value of the joystick for Halo Drive
     * @return The Rotation value of the joystick.
     */
    public double getHaloDrive_Rotation()
    {
        double value = 0;

        value = right.getAxis(Joystick.AxisType.kX);

        if(value < DEADBAND)
        {
            value = 0;
        }
        return value;
    }

    /**
     * Get the swerve vector (mag & angle) of the velocity joystick for Halo Drive
     * @return The vector of the joystick.
     */
    public SwerveVector getHaloDrive_Velocity()
    {
        SwerveVector value;
        
        value = new SwerveVector(left.getX(),
                                 left.getY());

        if(value.getMag() < DEADBAND)
        {
            value.setMag(0);
        }
        
        return value;
    }

    /**
     * Get the heading/angle in degrees for Angle Drive
     * @return The angle in degrees of the joystick.
     */
    public double getAngleDrive_Heading()
    {
        return right.getDirectionDegrees();
    }

    /**
     * Get the swerve vector (mag & angle) of the velocity joystick for Angle Drive
     * @return The vector of the joystick.
     */
    public SwerveVector getAngleDrive_Velocity()
    {
        SwerveVector value;

        value = new SwerveVector(left.getX(),
                                 left.getY());

        if(value.getMag() < DEADBAND)
        {
            value.setMag(0);
        }
        return value;
    }
    
    /**
     * Get whether the high gear should be enabled
     * @return true if high gear, else low gear
     */
    public boolean getHighGearEnable()
    {
        // shift to high gear if the button is held down
        return left.getRawButton(2);
    }
    
    /**
     * Get the brake
     * @return
     */
    public boolean getBrake()
    {
        return left.getRawButton(3);
    }
    
    public boolean getResetGyro()
    {
        return left.getRawButton(7);
    }
}
