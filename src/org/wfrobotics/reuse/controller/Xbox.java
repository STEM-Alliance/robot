package org.wfrobotics.reuse.controller;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;

import edu.wpi.first.wpilibj.XboxController;

// TODO wrap instead of subclass

/**
 * 
 * @author Team 4818 WFRobotics
 *
 */
public class Xbox extends XboxController {

    public static enum AXIS {
        LEFT_X(0),
        LEFT_Y(1),
        LEFT_TRIGGER(2),
        RIGHT_TRIGGER(3),
        RIGHT_X(4),
        RIGHT_Y(5);

        public final int value;

        private AXIS(int value) { this.value = value; }
        public int get() { return value; }
    }

    public static enum BUTTON {
        A(1),
        B(2),
        X(3),
        Y(4),
        LB(5),
        RB(6),
        BACK(7),
        START(8),
        LEFT_STICK(9),
        RIGHT_STICK(10);

        private final int value;

        private BUTTON(int value) { this.value = value; }
        public int get() { return value; }
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
     * @param axis Axis Number
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
     * @param axis AxisType
     * @return
     */
    public double getAxis(AXIS axis)
    {
        // we need forward Y to be positive instead of negative
        if(axis == AXIS.LEFT_Y || axis == AXIS.RIGHT_Y)
        {
            return -getRawAxis(axis.get());
        }
        return getRawAxis(axis.get());
    }

    /**
     * Retrieve value for X axis
     * 
     * @param hand Hand associated with the Joystick
     * @return Value of Axis (-1 to 1)
     */
    public double getX(Hand hand)
    {
        if (hand.value == Hand.kRight.value)
        {
            return getAxis(AXIS.RIGHT_X);
        }
        else if (hand.value == Hand.kLeft.value)
        {
            return getAxis(AXIS.LEFT_X);
        }
        return 0;
    }

    /**
     * Retrieve value for Y axis
     * 
     * @param hand Hand associated with the Joystick
     * @return Value of Axis (-1 to 1)
     */
    public double getY(Hand hand)
    {
        if (hand.value == Hand.kRight.value)
        {
            return getAxis(AXIS.RIGHT_Y);
        }
        else if (hand.value == Hand.kLeft.value)
        {
            return getAxis(AXIS.LEFT_Y);
        }
        return 0;
    }

    /**
     * Get the magnitude of the direction vector formed by the joystick's
     * current position relative to its origin
     * 
     * @param hand Hand associated with the Joystick
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
     * @param hand Hand associated with the Joystick
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
     * @param hand Hand associated with the Joystick
     * @return The direction of the vector in degrees
     */
    public double getDirectionDegrees(Hand hand)
    {
        double Angle = Math.toDegrees(getDirectionRadians(hand));
        return Utilities.wrapToRange(Angle + 90, -180, 180);
    }

    /**
     * Get the vector formed by the hand
     * @param hand Hand associated with the Joystick
     * @return Vector
     */
    public Vector getVector(Hand hand)
    {
        return new Vector(getX(hand), getY(hand));
    }


    /**
     * Get Value from a button
     * 
     * @param button Button Type
     * @return
     */
    public boolean getButton(BUTTON button)
    {
        return getRawButton(button.value);
    }

    /**
     * Get Trigger Value as Button
     * 
     * @param hand Hand associated with button
     * @return false
     */
    @Override
    public boolean getTrigger(Hand hand)
    {
        if (hand == Hand.kLeft)
        {
            return getAxis(AXIS.LEFT_TRIGGER) > 0.6;
        }
        else if (hand == Hand.kRight)
        {
            return getAxis(AXIS.RIGHT_TRIGGER) > 0.6;
        }
        return false;
    }

    @Override
    public int getPOV(int pov)
    {
        int Angle = super.getPOV(pov);
        if (Angle != -1)
        {
            return (int) Utilities.wrapToRange(Angle, 0, 360);
        }
        return -1;
    }

    public boolean getJoystickButton(Hand hand)
    {
        return super.getStickButton(hand);
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