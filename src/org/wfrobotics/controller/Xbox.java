package org.wfrobotics.controller;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;

import edu.wpi.first.wpilibj.XboxController;

/**
 * 
 * @author Team 4818 WFRobotics
 *
 */
public class Xbox extends XboxController {

    public static class AxisType {
        public final int value;
        
        private static final int kLeftX_val = 0;
        private static final int kLeftY_val = 1;
        private static final int kLeftTrigger_val = 2;
        private static final int kRightTrigger_val = 3;
        private static final int kRightX_val = 4;
        private static final int kRightY_val = 5;
        
        private AxisType(int value) { this.value = value; }
        
        public static final AxisType kLeftX = new AxisType(kLeftX_val);
        public static final AxisType kLeftY = new AxisType(kLeftY_val);
        public static final AxisType kLeftTrigger = new AxisType(kLeftTrigger_val);
        public static final AxisType kRightTrigger = new AxisType(kRightTrigger_val);        
        public static final AxisType kRightX = new AxisType(kRightX_val);
        public static final AxisType kRightY = new AxisType(kRightY_val);
    }
    
    public static class ButtonType {
        public final int value;
        
        private static final int kA_val = 1;
        private static final int kB_val = 2;
        private static final int kX_val = 3;
        private static final int kY_val = 4;
        private static final int kLB_val = 5;
        private static final int kRB_val = 6;
        private static final int kBack_val = 7;
        private static final int kStart_val = 8;
        private static final int kLeftStick_val = 9;
        private static final int kRightStick_val = 10;

        private ButtonType(int value) { this.value = value; }

        public static final ButtonType kLeftStick = new ButtonType(kLeftStick_val);
        public static final ButtonType kRightStick = new ButtonType(kRightStick_val);
        public static final ButtonType kX = new ButtonType(kX_val);
        public static final ButtonType kY = new ButtonType(kY_val);
        public static final ButtonType kA = new ButtonType(kA_val);
        public static final ButtonType kB = new ButtonType(kB_val);
        public static final ButtonType kRB = new ButtonType(kRB_val);
        public static final ButtonType kLB = new ButtonType(kLB_val);
        public static final ButtonType kStart = new ButtonType(kStart_val);
        public static final ButtonType kBack = new ButtonType(kBack_val);
    }


        
    private static final double DEADBAND = 0.2;

    
    /**
     * Constructor
     * 
     * @param port
     *            USB Port on DriverStation
     */
    public Xbox(int port)
    {
        super(port);
    }

    /**
     * Get Value from an Axis
     * 
     * @param axis
     *            Axis Number
     * @return Value from Axis (-1 to 1)
     */
    @Override
    public double getRawAxis(int axis)
    {
        return scaleForDeadband(super.getRawAxis(axis));
    }

    /**
     * Get Value from an Axis
     * 
     * @param axis
     *            AxisType
     * @return
     */
    public double getAxis(AxisType axis)
    {
        // we need forward Y to be positive instead of negative
        if(axis == AxisType.kLeftY || axis == AxisType.kRightY)
            return -getRawAxis(axis.value);
        else
            return getRawAxis(axis.value);
    }

    /**
     * Retrieve value for X axis
     * 
     * @param hand
     *            Hand associated with the Joystick
     * @return Value of Axis (-1 to 1)
     */
    public double getX(Hand hand)
    {
        if (hand.value == Hand.kRight.value)
        {
            return getAxis(AxisType.kRightX);
        }
        else if (hand.value == Hand.kLeft.value)
        {
            return getAxis(AxisType.kLeftX);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Retrieve value for Y axis
     * 
     * @param hand
     *            Hand associated with the Joystick
     * @return Value of Axis (-1 to 1)
     */
    public double getY(Hand hand)
    {
        if (hand.value == Hand.kRight.value)
        {
            return getAxis(AxisType.kRightY);
        }
        else if (hand.value == Hand.kLeft.value)
        {
            return getAxis(AxisType.kLeftY);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Unused
     * 
     * @param hand
     *            Unused
     * @return 0
     */
    public double getZ(Hand hand)
    {
        return 0;
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
        return Math.sqrt(Math.pow(getX(hand), 2) + Math.pow(getY(hand), 2));
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
        return Math.atan2(getY(hand), getX(hand));
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
        double Angle = Math.toDegrees(getDirectionRadians(hand));
        return Utilities.wrapToRange(Angle + 90, -180, 180);
    }
    
    /**
     * Get the vector formed by the hand
     * @param hand
     *            Hand associated with the Joystick
     * @return Vector
     */
    public Vector getVector(Hand hand)
    {
        return new Vector(getX(hand), getY(hand));
    }


    /**
     * Get Value from a button
     * 
     * @param button
     *            Button Type
     * @return
     */
    public boolean getButton(ButtonType button)
    {
        return getRawButton(button.value);
    }

    /**
     * Get Trigger Value as Button
     * 
     * @param hand
     *            Hand associated with button
     * @return false
     */
    @Override
    public boolean getTrigger(Hand hand)
    {
        if (hand == Hand.kLeft)
        {
            return getAxis(AxisType.kLeftTrigger) > 0.6;
        }
        else if (hand == Hand.kRight)
        {
            return getAxis(AxisType.kRightTrigger) > 0.6;
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public int getPOV(int pov)
    {
        int Angle = super.getPOV(pov);
        if (Angle != -1)
        {
            return (int) Utilities.wrapToRange(Angle, 0, 360);
        }
        else
        {
            return -1;
        }
    }
    
    private double scaleForDeadband(double value)
    {
        double abs = Math.abs(value);
        
        if (abs < DEADBAND)
        {
            value = 0;
        }
        else
        {
            value = Math.signum(value) * ((abs - DEADBAND) / (1 - DEADBAND));
        }
        
        return value;
    }

}