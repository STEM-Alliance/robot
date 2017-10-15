package org.wfrobotics.reuse.controller;

import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.reuse.utilities.Utilities;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.XboxController;

/**
 * @author Team 4818 WFRobotics
 */
public class Xbox
{
    public static enum AXIS
    {
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

    public static enum BUTTON
    {
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
    private final XboxController hw;

    public Xbox(int driveStationUSBPort)
    {
        hw = new XboxController(driveStationUSBPort);
    }

    /**
     * Get value
     * @param axis type
     * @return axis value
     */
    public double getAxis(AXIS axis)
    {
        if(axis == AXIS.LEFT_Y || axis == AXIS.RIGHT_Y)
        {
            // Want forward Y to be positive instead of negative
            return -getRawAxis(axis.get());
        }
        return getRawAxis(axis.get());
    }

    /**
     * Get value
     * @param side joystick side
     * @return value (-1 to 1)
     */
    public double getX(Hand side)
    {
        return (side == Hand.kLeft) ? getAxis(AXIS.LEFT_X) : getAxis(AXIS.RIGHT_X);
    }

    /**
     * Get value
     * @param side joystick side
     * @return value (-1 to 1)
     */
    public double getY(Hand side)
    {
        return (side == Hand.kLeft) ? getAxis(AXIS.LEFT_Y) : getAxis(AXIS.RIGHT_Y);
    }

    public double getMagnitude(Hand side)
    {
        double x = getX(side);
        double y = getY(side);
        return Math.sqrt(x * x + y * y);
    }

    public double getAngleDegrees(Hand side)
    {
        double radians = Math.atan2(getY(side), getX(side));
        double Angle = Math.toDegrees(radians);
        return Utilities.wrapToRange(Angle + 90, -180, 180);
    }

    /**
     * Magnitude and angle
     * @param joystick side
     * @return
     */
    public HerdVector getVector(Hand side)
    {
        double x = getX(side);
        double y = getY(side);
        return new HerdVector(Math.sqrt(x * x + y * y), Math.atan2(y, x) * 180 / Math.PI);
    }

    /**
     * Get value
     * @param side
     * @return 0 to 1
     */
    public double getTrigger(Hand side)
    {
        return hw.getTriggerAxis(side);
    }

    public boolean getButtonPressed(BUTTON button)
    {
        return hw.getRawButton(button.value);
    }

    public boolean getTriggerPressed(Hand side)
    {
        return (side == Hand.kLeft) ? getAxis(AXIS.LEFT_TRIGGER) > 0.6 : getAxis(AXIS.RIGHT_TRIGGER) > 0.6;
    }

    public int getDpad()
    {
        int angle = hw.getPOV(0);
        if (angle != -1)
        {
            return (int) Utilities.wrapToRange(angle, 0, 360);
        }
        return -1;
    }

    public void setRumble(Hand side, double value)
    {
        RumbleType r = (side == Hand.kLeft) ? RumbleType.kLeftRumble : RumbleType.kRightRumble;
        hw.setRumble(r, value);
    }

    private double getRawAxis(int axis)
    {
        return scaleForDeadband(hw.getRawAxis(axis));  // -1 to 1
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