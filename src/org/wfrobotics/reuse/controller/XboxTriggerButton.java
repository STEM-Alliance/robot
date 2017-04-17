package org.wfrobotics.reuse.controller;

import edu.wpi.first.wpilibj.buttons.Button;
/**
 * Xbox trigger button, uses trigger as button source
 * @author bpoppe
 *
 */
public class XboxTriggerButton extends Button
{
    private final Xbox hardware;
    private final Xbox.Hand hand;
    private final double limit;
    
    /**
     * Constructor, Button with XBox trigger as source
     * @param hardware Which XBox Controller
     * @param hand Side of the controller this trigger is on
     * @param thresholdOn Percentage pressed that counts as the button being pressed (range: 0 - 1)
     */
    public XboxTriggerButton(Xbox hardware, Xbox.Hand hand, double thresholdOn) 
    {
        this.hardware = hardware;
        this.hand = hand;
        this.limit = thresholdOn;
    }

    public boolean get()
    {
        return hardware.getTriggerAxis(hand) > limit;
    }
}
