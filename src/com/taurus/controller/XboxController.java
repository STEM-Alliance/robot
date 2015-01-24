package com.taurus.controller;

import com.taurus.Utilities;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;

/**
 * Adapted from Team 3946 (https://github.com/frc3946)
 * @author Team 4818 Taurus Robotics
 *
 */
public class XboxController extends GenericHID {

    private DriverStation m_ds;
    private final int m_port;

    /**
     * Represents an analog axis on a joystick.
     */
    public static class AxisType {

        /**
         * The integer value representing this enumeration
         */
        public final int value;
        private static final int kLeftX_val = 0;
        private static final int kLeftY_val = 1;
        private static final int kLeftTrigger_val = 2;
        private static final int kRightTrigger_val = 3;
        private static final int kRightX_val = 4;
        private static final int kRightY_val = 5;

        private AxisType(int value) {
            this.value = value;
        }

        /**
         * Axis: Left X
         */
        public static final AxisType kLeftX = new AxisType(kLeftX_val);

        /**
         * Axis: Left Y
         */
        public static final AxisType kLeftY = new AxisType(kLeftY_val);

        /**
         * Axis: Left Trigger
         */
        public static final AxisType kLeftTrigger = new AxisType(kLeftTrigger_val);
        
        /**
         * Axis: Right Trigger
         */
        public static final AxisType kRightTrigger = new AxisType(kRightTrigger_val);

        /**
         * Axis: Right X
         */
        public static final AxisType kRightX = new AxisType(kRightX_val);

        /**
         * Axis: Right Y
         */
        public static final AxisType kRightY = new AxisType(kRightY_val);

    }

    /**
     * Represents a digital button on a joystick.
     */
    public static class ButtonType {

        /**
         * The integer value representing this enumeration
         */
        public final int value;
        private static final int kA_val = 1;
        private static final int kB_val = 2;
        private static final int kX_val = 3;
        private static final int kY_val = 4;
        private static final int kL_val = 5;
        private static final int kR_val = 6;
        private static final int kBack_val = 7;
        private static final int kStart_val = 8;
        private static final int kLeftStick_val = 9;
        private static final int kRightStick_val = 10;

        private ButtonType(int value) {
            this.value = value;
        }

        /**
         * Button: X-Joystick
         */
        public static final ButtonType kLeftStick = new ButtonType(kLeftStick_val);

        /**
         * Button: Y-Joystick
         */
        public static final ButtonType kRightStick = new ButtonType(kRightStick_val);

        /**
         * Button: X
         */
        public static final ButtonType kX = new ButtonType(kX_val);

        /**
         * Button: Y
         */
        public static final ButtonType kY = new ButtonType(kY_val);

        /**
         * Button: A
         */
        public static final ButtonType kA = new ButtonType(kA_val);

        /**
         * Button: B
         */
        public static final ButtonType kB = new ButtonType(kB_val);

        /**
         * Button: R1
         */
        public static final ButtonType kR = new ButtonType(kR_val);

        /**
         * Button: L1
         */
        public static final ButtonType kL = new ButtonType(kL_val);

        /**
         * Button: Select
         */
        public static final ButtonType kStart = new ButtonType(kStart_val);

        /**
         * Button: Start
         */
        public static final ButtonType kBack = new ButtonType(kBack_val);
    }


    /**
     * Constructor
     * @param port USB Port on DriverStation
     */
    public XboxController(int port) {
        super();
        m_port = port;
        m_ds = DriverStation.getInstance();
    }

    /**
     * Get Value from an Axis
     * @param axis Axis Number
     * @return Value from Axis (-1 to 1)
     */
    public double getRawAxis(int axis) {
        return m_ds.getStickAxis(m_port, axis);
    }

    /**
     * Get Value from an Axis
     * @param axis AxisType
     * @return 
     */
    public double getAxis(AxisType axis) {
        return getRawAxis(axis.value);
    }

    /**
     * Retrieve value for X axis
     * @param hand Hand associated with the Joystick
     * @return Value of Axis (-1 to 1)
     */
    public double getX(Hand hand) {
        if(hand.value == Hand.kRight.value) {
            return getAxis(AxisType.kRightX);
        } else if(hand.value == Hand.kLeft.value) {
            return getAxis(AxisType.kLeftX);
        } else {
            return 0;
        }
    }

    /**
     * Retrieve value for Y axis
     * @param hand Hand associated with the Joystick
     * @return Value of Axis (-1 to 1)
     */
    public double getY(Hand hand) {
        if(hand.value == Hand.kRight.value) {
            return getAxis(AxisType.kRightY);
        } else if(hand.value == Hand.kLeft.value) {
            return getAxis(AxisType.kLeftY);
        } else {
            return 0;
        }
    }

    /**
     * Unused
     * @param hand Unused
     * @return 0
     */
    public double getZ(Hand hand) {
        return 0;
    }
    
    /**
     * Get the magnitude of the direction vector formed by the joystick's current position relative to its origin
     * @param hand Hand associated with the Joystick
     * @return the magnitude of the direction vector
     */
    public double getMagnitude(Hand hand) {
        return Math.sqrt(Math.pow(getX(hand), 2) + Math.pow(getY(hand), 2));
    }

    /**
     * Get the direction of the vector formed by the joystick and its origin in radians
     * @param hand Hand associated with the Joystick
     * @return The direction of the vector in radians
     */
    public double getDirectionRadians(Hand hand) {
        return Math.atan2(getY(hand), getX(hand));
    }
    
    /**
     * Get the direction of the vector formed by the joystick and its origin in degrees
     * @param hand Hand associated with the Joystick
     * @return The direction of the vector in degrees
     */
    public double getDirectionDegrees(Hand hand) {
        double Angle =  Math.toDegrees(getDirectionRadians(hand));
        return Utilities.wrapToRange(Angle + 90,-180,  180);
        
    }

    /**
     * Gets Value from D-Pad Left and Right Axis
     * @return Axis Value (-1 to 1)
     */
    public double getTwist() {
        return 0;
    }

    /**
     * Gets Value from Back Triggers
     * @return Axis Value (-1 to 1)
     */
    public double getThrottle() {
        return getAxis(AxisType.kRightTrigger);
    }

    /**
     * Gets value from a button
     * @param button number of the button 
     * @return State of the button
     */
    public boolean getRawButton(int button) {
        return ((0x1 << (button - 1)) & m_ds.getStickButtons(m_port)) != 0;
    }

    /**
     * Get Value from a button
     * @param button Button Type
     * @return 
     */
    public boolean getButton(ButtonType button) {
        return getRawButton(button.value);
    }

    /**
     * Get Trigger Value as Button
     * @param hand Hand associated with button
     * @return false
     */
    public boolean getTrigger(Hand hand) {
        if(hand == Hand.kLeft) {
            return getAxis(AxisType.kLeftTrigger) > 0.6;
        } else if(hand == Hand.kRight) {
            return getAxis(AxisType.kRightTrigger) > 0.6;
        } else {
            return false;
        }
    }
    
    /**
     * Get Trigger Value as Button
     * @param hand Hand associated with button
     * @return false
     */
    public double getTriggerVal(Hand hand) {
        if(hand == Hand.kLeft) {
            return getAxis(AxisType.kLeftTrigger);
        } else if(hand == Hand.kRight) {
            return getAxis(AxisType.kRightTrigger);
        } else {
            return 0;
        }
    }

    /**
     * Get Button from Joystick
     * @param hand hand associated with the button
     * @return Button Status (true or false)
     */
    public boolean getTop(Hand hand) {
        if(hand == Hand.kRight) {
            return getButton(ButtonType.kRightStick);
        } else if(hand == Hand.kLeft) {
            return getButton(ButtonType.kLeftStick);
        } else {
            return false;
        }
    }

    /**
     * Get Value from Back buttons
     * @param hand hand associated with the button
     * @return state of left or right 
     */
    public boolean getBumper(Hand hand) {
        if(hand == Hand.kRight) {
            return getButton(ButtonType.kR);
        } else if(hand == Hand.kLeft) {
            return getButton(ButtonType.kL);
        } else {
            return false;
        }
    }

    /**
     * Get State of Select Button
     * @return State of button
     */
    public boolean getStart() {
        return getButton(ButtonType.kStart);
    }

    /**
     * Get State of Back Button
     * @return State of button
     */
    public boolean getBack() {
        return getButton(ButtonType.kBack);
    }

    /**
     * Get State of A Button
     * @return State of button
     */
    public boolean getAButton() {
        return getButton(ButtonType.kA);
    }

    /**
     * Get State of B Button
     * @return State of button
     */
    public boolean getBButton() {
        return getButton(ButtonType.kB);
    }

    /**
     * Get State of X Button
     * @return State of button
     */
    public boolean getXButton() {
        return getButton(ButtonType.kX);
    }

    /**
     * Get State of Y Button
     * @return State of button
     */
    public boolean getYButton() {
        return getButton(ButtonType.kY);
    }

    @Override
    public int getPOV(int pov) {
        return m_ds.getStickPOV(m_port, pov);
    }
}