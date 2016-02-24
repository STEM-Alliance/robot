package com.taurus.controller;

import edu.wpi.first.wpilibj.buttons.Button;
/**
 * Xbox button for use with Command based robot
 * @author drlindne
 *
 */
public class XboxTriggerButton extends Button
{
    Xbox hardware;
    Xbox.Hand hand;
    
    public XboxTriggerButton(Xbox hardware, Xbox.Hand hand) {
        this.hardware = hardware;
        this.hand = hand;
    }

    public boolean get()
    {
        return hardware.getTrigger(hand);
    }
}
