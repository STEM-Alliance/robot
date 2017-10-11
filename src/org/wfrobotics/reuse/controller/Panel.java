package org.wfrobotics.reuse.controller;

import org.wfrobotics.Utilities;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.hal.HAL;

public class Panel extends XboxController{

    public static enum AXIS {
        DIAL_TOP_L     (0),
        DIAL_TOP_R     (1),
        SLIDER_L       (2),
        SLIDER_R       (3),
        DIAL_BOTTOM_L  (4),
        DIAL_BOTTOM_R  (5);

        private final int value;

        private AXIS(int value) { this.value = value; }
        public int get() { return value; }
    }

    public static enum BUTTON {
        GREEN_B  (1),
        GREEN_T  (2),
        YELLOW_B (3),
        YELLOW_T (4),
        SWITCH_L (5),
        WHITE_B  (6),
        WHITE_T  (7),
        BLACK_B  (8),
        BLACK_T  (9),
        SWITCH_R (10);

        private final int value;

        private BUTTON(int value) { this.value = value; }
        public int get() { return value; }
    }

    public static enum COLOR
    {
        BLACK(0),
        RED(1),
        BLUE(2),
        GREEN(3);

        private final int value;

        private COLOR(int value) { this.value = value; }
        public int get() { return value; }
    }

    private short m_LEDMsgL;
    private short m_LEDMsgR;

    /**
     * Constructor
     * 
     * @param port USB Port on DriverStation
     */
    public Panel(int port)
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
        return super.getRawAxis(axis);
    }

    /**
     * Get Value from an Axis
     * 
     * @param axis AxisType
     * @return Value from Axis (-1 to 1)
     */
    public double getAxis(AXIS axis)
    {
        return getRawAxis(axis.get());
    }

    /**
     * Retrieve value for slider
     * 
     * @param hand Hand associated with the Slider
     * @return Value of Axis (-1 to 1)
     */
    public double getSlider(Hand hand)
    {
        if (hand.value == Hand.kRight.value)
        {
            return getAxis(AXIS.SLIDER_R);
        }
        else if (hand.value == Hand.kLeft.value)
        {
            return getAxis(AXIS.SLIDER_L);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Retrieve value for the top dial
     * 
     * @param hand Hand associated with the dial
     * @return Value of Axis (-1 to 1)
     */
    public double getTopDial(Hand hand)
    {
        if (hand.value == Hand.kRight.value)
        {
            return getAxis(AXIS.DIAL_TOP_R);
        }
        else if (hand.value == Hand.kLeft.value)
        {
            return getAxis(AXIS.DIAL_TOP_L);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Retrieve value for the bottom dial
     * 
     * @param hand Hand associated with the dial
     * @return Value of Axis (-1 to 1)
     */
    public double getBottomDial(Hand hand)
    {
        if (hand.value == Hand.kRight.value)
        {
            return getAxis(AXIS.DIAL_BOTTOM_R);
        }
        else if (hand.value == Hand.kLeft.value)
        {
            return getAxis(AXIS.DIAL_BOTTOM_L);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Get Value from a button
     * 
     * @param button Button Type
     * @return
     */
    public boolean getButton(BUTTON button)
    {
        return getRawButton(button.get());
    }

    /**
     * Get the Rotary encoder as values from 0-7
     * @return
     */
    public int getRotary()
    {
        int Angle = super.getPOV(0);
        if (Angle != -1)
        {
            return (int) Utilities.wrapToRange(Angle, 0, 360) / 45;
        }
        return -1;
    }

    /**
     * Set the LEDs on the panel
     * @param hand
     * @param LED1
     * @param LED2
     * @param LED3
     * @param LED4
     */
    public void setLEDs(Hand hand, COLOR LED1, COLOR LED2, COLOR LED3, COLOR LED4)
    {
        // this value might need to be multiplied by 256
        if(hand == Hand.kLeft)
            m_LEDMsgL = (short) (LED1.get() << 6 | LED2.get() << 4 | LED3.get() << 2 | LED4.get());
        if(hand == Hand.kRight)
            m_LEDMsgR = (short) (LED1.get() << 6 | LED2.get() << 4 | LED3.get() << 2 | LED4.get());
        HAL.setJoystickOutputs((byte) getPort(), 0, m_LEDMsgL, m_LEDMsgR);
    }
}
