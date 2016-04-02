package org.wfrobotics.controller;

import org.wfrobotics.controller.Xbox.RumbleType;

import edu.wpi.first.wpilibj.GenericHID.Hand;

public class RockerController {

    private Xbox xbox;

    public static final double DEADBAND = 0.2;

    
    /**
     * Create a new Xbox Controller object
     */
    public RockerController()
    {
        xbox = new Xbox(0);

    }
    
    private double scale(double value)
    {
        double abs = Math.abs(value);
        
        if (abs < DEADBAND)
        {
            value = 0;
        }
        else
        {
            value = Math.signum(value) * ((abs - DEADBAND) / (1.0 - DEADBAND));
        }
        
        return value;
    }
    
    /**
     * {@inheritDoc}
     */
    public double getX(Hand hand)
    {
        return scale(xbox.getX(hand));
    }

    /**
     * {@inheritDoc}
     */
    public double getY(Hand hand)
    {
        return scale(xbox.getY(hand));
    }

    /**
     * {@inheritDoc}
     */
    public double getMagnitude(Hand hand)
    {
        return scale(xbox.getMagnitude(hand));
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
    public boolean getTrigger(Hand hand)
    {
        return xbox.getTrigger(hand);
    }
    
    public double getTriggerVal(Hand hand)
    {
        return xbox.getTriggerVal(hand);
    }
    
    public boolean getBumper(Hand hand)
    {
        return xbox.getBumper(hand);
    }
    
    public boolean getAButton()
    {
        return xbox.getAButton();
    }
    
    public boolean getBButton()
    {
        return xbox.getBButton();
    }
    
    public boolean getXButton()
    {
        return xbox.getXButton();
    }

    public boolean getYButton()
    {
        return xbox.getYButton();
    }
    
    
    /**
     * {@inheritDoc}
     */
    public double getDPad()
    {
        return xbox.getPOV();
    }

    public void setRumble(Hand hand, float value)
    {
        xbox.setRumble(hand == Hand.kLeft ? RumbleType.kLeftRumble : RumbleType.kRightRumble, value);
    }

}
