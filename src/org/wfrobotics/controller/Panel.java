package org.wfrobotics.controller;

import edu.wpi.first.wpilibj.DriverStation;

public class Panel {

    private DriverStation m_ds;
    private final int m_port;
        
    /**
     * Represents an analog axis on the panel.
     */
    public static class AxisType {

        /**
         * The integer value representing this enumeration
         */
        public final int value;
        private static final int kSliderLeft_val = 0;
        private static final int kSliderRight_val = 1;
        private static final int kKnobLeft_val = 2;
        private static final int kKnobRight_val = 3;

        private AxisType(int value)
        {
            this.value = value;
        }

        public static final AxisType kSliderLeft = new AxisType(kSliderLeft_val);

        public static final AxisType kSliderRight = new AxisType(kSliderRight_val);

        public static final AxisType kKnobLeft = new AxisType(kKnobLeft_val);

        public static final AxisType kKnobRight = new AxisType(kKnobRight_val);
    }

    /**
     * Represents a digital button on the panel
     */
    public static class ButtonType {

        /**
         * The integer value representing this enumeration
         */
        public final int value;
        private static final int kYellowR_val = 1;
        private static final int kGreenR_val = 2;
        private static final int kBlackR_val = 3;
        private static final int kWhiteR_val = 4;
        private static final int kYellowL_val = 5;
        private static final int kGreenL_val = 6;
        private static final int kBlackL_val = 7;
        private static final int kWhiteL_val = 8;
        private static final int kSwitchR_val = 9;
        private static final int kSwitchL_val = 10;
        private static final int kKey_val = 11;
        private static final int kFoot_val = 12;

        private ButtonType(int value)
        {
            this.value = value;
        }

        public static final ButtonType kYellowR = new ButtonType(kYellowR_val);
        
        public static final ButtonType kGreenR = new ButtonType(kGreenR_val);
        
        public static final ButtonType kBlackR = new ButtonType(kBlackR_val);
        
        public static final ButtonType kWhiteR = new ButtonType(kWhiteR_val);


        public static final ButtonType kYellowL = new ButtonType(kYellowL_val);
        
        public static final ButtonType kGreenL = new ButtonType(kGreenL_val);
        
        public static final ButtonType kBlackL = new ButtonType(kBlackL_val);
        
        public static final ButtonType kWhiteL = new ButtonType(kWhiteL_val);
        
        public static final ButtonType kSwitchR = new ButtonType(kSwitchR_val);

        public static final ButtonType kSwitchL = new ButtonType(kSwitchL_val);

        public static final ButtonType kKey = new ButtonType(kKey_val);
        
        public static final ButtonType kFoot = new ButtonType(kFoot_val);
    }
    
    public Panel(int port)
    {
        super();
        m_port = port;
        m_ds = DriverStation.getInstance();
    }

    /**
     * Get Value from an Axis
     * 
     * @param axis
     *            Axis Number
     * @return Value from Axis (-1 to 1)
     */
    public double getRawAxis(int axis)
    {
        return m_ds.getStickAxis(m_port, axis);
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
        return getRawAxis(axis.value);
    }
    
    /**
     * Gets value from a button
     * 
     * @param button
     *            number of the button
     * @return State of the button
     */
    public boolean getRawButton(int button)
    {
        return ((0x1 << (button - 1)) & m_ds.getStickButtons(m_port)) != 0;
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
     * Get Value from the left slider
     * @return value from 0 to 1
     */
    public double getSliderLeft()
    {
        return getAxis(AxisType.kSliderLeft);
    }

    /**
     * Get Value from the left knob
     * @return value from 0 to 1
     */
    public double getKnobLeft()
    {
        return getAxis(AxisType.kKnobLeft);
    }
    
    /**
     * Get Value from the Right slider
     * @return value from 0 to 1
     */
    public double getSliderRight()
    {
        return getAxis(AxisType.kSliderRight);
    }

    /**
     * Get Value from the Right knob
     * @return value from 0 to 1
     */
    public double getKnobRight()
    {
        return getAxis(AxisType.kKnobRight);
    }

    /**
     * Get State of Green Right Button
     * 
     * @return State of button
     */
    public boolean getGreenRButton()
    {
        return getButton(ButtonType.kGreenR);
    }

    /**
     * Get State of Yellow Right Button
     * 
     * @return State of button
     */
    public boolean getYellowRButton()
    {
        return getButton(ButtonType.kYellowR);
    }

    /**
     * Get State of Black Right Button
     * 
     * @return State of button
     */
    public boolean getBlackRButton()
    {
        return getButton(ButtonType.kBlackR);
    }

    /**
     * Get State of White Right Button
     * 
     * @return State of button
     */
    public boolean getWhiteRButton()
    {
        return getButton(ButtonType.kWhiteR);
    }

    /**
     * Get State of Switch Right
     * 
     * @return State of switch
     */
    public boolean getSwitchR()
    {
        return getButton(ButtonType.kSwitchR);
    }

    /**
     * Get State of Green Left Button
     * 
     * @return State of button
     */
    public boolean getGreenLButton()
    {
        return getButton(ButtonType.kGreenL);
    }

    /**
     * Get State of Yellow Left Button
     * 
     * @return State of button
     */
    public boolean getYellowLButton()
    {
        return getButton(ButtonType.kYellowL);
    }

    /**
     * Get State of Black Left Button
     * 
     * @return State of button
     */
    public boolean getBlackLButton()
    {
        return getButton(ButtonType.kBlackL);
    }

    /**
     * Get State of White Left Button
     * 
     * @return State of button
     */
    public boolean getWhiteLButton()
    {
        return getButton(ButtonType.kWhiteL);
    }

    /**
     * Get State of Switch Right
     * 
     * @return State of switch
     */
    public boolean getSwitchL()
    {
        return getButton(ButtonType.kSwitchL);
    }
    
    /**
     * Get State of Key Switch
     * 
     * @return State of switch
     */
    public boolean getKeySwitch()
    {
        return getButton(ButtonType.kKey);
    }
    
    /**
     * Get State of Foot Switch
     * 
     * @return State of switch
     */
    public boolean getFootSwitch()
    {
        return getButton(ButtonType.kFoot);
    }

    /**
     * Get State of the Rotary Switch.
     * The switch has 6 positions, so that gives each position 60 degrees.
     * ie Position 0: 0 degrees;
     *    Position 1: 60 degrees;
     *    etc
     * 
     * @return value from 0 to 360 of switch
     */
    public int getRotary()
    {
        return m_ds.getStickPOV(m_port, 0);
    }
    
    
}
