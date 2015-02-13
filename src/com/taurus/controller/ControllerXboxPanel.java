package com.taurus.controller;

import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class ControllerXboxPanel implements Controller {

    private Xbox xbox;
    private Panel panel;
    
    public static final double DEADBAND = 0.2;

    private boolean fieldRelative;
    private boolean fieldRelativeLast;

    /**
     * Create a new Xbox Controller object
     */
    public ControllerXboxPanel()
    {
        xbox = new Xbox(0);
        panel = new Panel(1);

        fieldRelative = true;
        fieldRelativeLast = false;
    }

    public double getX(Hand hand)
    {
        return xbox.getX(hand);
    }

    public double getY(Hand hand)
    {
        return xbox.getY(hand);
    }

    /**
     * Get the magnitude of the direction vector formed by the joystick's
     * current position relative to its origin
     * 
     * @param hand
     *            Hand associated with the Joystick
     * @return the magnitude of the direction vector
     */
    public double getMagnitude(Hand hand)
    {
        double value = 0;

        value = xbox.getMagnitude(hand);

        if (value < DEADBAND)
        {
            value = 0;
        }
        return value;
    }

    /**
     * Get the direction of the vector formed by the joystick and its origin in
     * degrees
     * 
     * @param hand
     *            Hand associated with the Joystick
     * @return The direction of the vector in degrees
     */
    public double getDirectionDegrees(Hand hand)
    {
        return xbox.getDirectionDegrees(hand);
    }

    /**
     * Get the direction of the vector formed by the joystick and its origin in
     * radians
     * 
     * @param hand
     *            Hand associated with the Joystick
     * @return The direction of the vector in radians
     */
    public double getDirectionRadians(Hand hand)
    {
        return xbox.getDirectionRadians(hand);
    }

    /**
     * Get the Rotation value of the joystick for Halo Drive
     * 
     * @return The Rotation value of the joystick.
     */
    public double getHaloDrive_Rotation()
    {
        double value = 0;

        value = xbox.getAxis(Xbox.AxisType.kRightX);

        if (Math.abs(value) < DEADBAND)
        {
            value = 0;
        }
        return value;
    }

    /**
     * Get the swerve vector (mag & angle) of the velocity joystick for Halo
     * Drive
     * 
     * @return The vector of the joystick.
     */
    public SwerveVector getHaloDrive_Velocity()
    {
        SwerveVector value;

        value = new SwerveVector(xbox.getX(Hand.kLeft), xbox.getY(Hand.kLeft));

        if (value.getMag() < DEADBAND)
        {
            value.setMag(0);
        }

        return value;
    }

    /**
     * Get the heading/angle in degrees for Angle Drive
     * 
     * @return The angle in degrees of the joystick.
     */
    public double getAngleDrive_Heading()
    {
        double Angle = -1;
        if (xbox.getMagnitude(Hand.kRight) > 0.65)
        {
            Angle = xbox.getDirectionDegrees(Hand.kRight);
        }

        return Angle;
    }

    /**
     * Get rotation from the DPad for Angle Drive
     * 
     * @return
     */
    public double getAngleDrive_Rotation()
    {
        double Rotation = 0;
        if (xbox.getPOV() == 90)
        {
            Rotation = .75;
        }
        else if (xbox.getPOV() == 270)
        {
            Rotation = -.75;
        }
        return Rotation;
    }

    /**
     * Get the swerve vector (mag & angle) of the velocity joystick for Angle
     * Drive
     * 
     * @return The vector of the joystick.
     */
    public SwerveVector getAngleDrive_Velocity()
    {
        SwerveVector value;

        value = new SwerveVector(xbox.getX(Hand.kLeft), xbox.getY(Hand.kLeft));

        if (value.getMag() < DEADBAND)
        {
            value.setMag(0);
        }
        return value;
    }

    public boolean getRawButtion(int button)
    {
        return xbox.getRawButton(button);
    }

    public double getDPad()
    {
        return xbox.getPOV();
    }
    
    /**
     * Get whether the high gear should be enabled
     * 
     * @return true if high gear, else low gear
     */
    public boolean getHighGearEnable()
    {
        return xbox.getBumper(Hand.kRight);
    }

    /**
     * Get the brake
     * 
     * @return
     */
    public boolean getBrake()
    {
        return xbox.getBButton();
    }

    public boolean getResetGyro()
    {
        return panel.getYellowLeftButton();
    }

    public boolean getFieldRelative()
    {
        if (!fieldRelativeLast && panel.getBlackLeftButton())
        {
            fieldRelative = !fieldRelative;
        }
        fieldRelativeLast = panel.getBlackLeftButton();
        return fieldRelative;
    }

    @Override
    public boolean getTrigger(Hand hand)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getAddChuteTote()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getAddFloorTote()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getAddContainer()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getEjectStack()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCarHome()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getCarTop()
    {
        // TODO Auto-generated method stub
        return false;
    }
}
