package com.taurus.controller;

import com.taurus.swerve.SwerveVector;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class RockerController {

    private Xbox xbox;

    public static final double DEADBAND = 0.35;

    
    /**
     * Create a new Xbox Controller object
     */
    public RockerController()
    {
        xbox = new Xbox(0);

    }

    /**
     * {@inheritDoc}
     */
    public double getX(Hand hand)
    {
        return xbox.getX(hand);
    }

    /**
     * {@inheritDoc}
     */
    public double getY(Hand hand)
    {
        return xbox.getY(hand);
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public double getDirectionDegrees(Hand hand)
    {
        return xbox.getDirectionDegrees(hand);
    }

    /**
     * {@inheritDoc}
     */
    public double getDirectionRadians(Hand hand)
    {
        return xbox.getDirectionRadians(hand);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getRawButton(int button)
    {
        return xbox.getRawButton(button);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getHighGearEnable()
    {
        return xbox.getBumper(Hand.kRight);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getTrigger(Hand hand)
    {
        return xbox.getTrigger(hand);
    }
    
    public boolean getBButton()
    {
        return xbox.getBButton();
    }
    
    public boolean getAButton()
    {
        return xbox.getAButton();
    }

    public double getTriggerVal(Hand hand)
    {
        return xbox.getTriggerVal(hand);
    }
    /**
     * {@inheritDoc}
     */
    public double getDPad()
    {
        return xbox.getPOV();
    }

    public boolean getWheelCal()
    {
        return false;
    }

}
