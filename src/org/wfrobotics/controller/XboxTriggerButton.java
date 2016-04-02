package org.wfrobotics.controller;

import edu.wpi.first.wpilibj.buttons.Button;
/**
 * Xbox trigger button for use with Command based robot
 * @author bpoppe
 *
 */
public class XboxTriggerButton extends Button
{
    Xbox hardware;
    Xbox.Hand hand;
    double limit = .6;
    
    public XboxTriggerButton(Xbox hardware, Xbox.Hand hand) {
        this(hardware, hand, .6);
    }
    
    public XboxTriggerButton(Xbox hardware, Xbox.Hand hand, double limit) {
        this.hardware = hardware;
        this.hand = hand;
        this.limit = limit;
    }

    public boolean get()
    {
        return hardware.getTriggerVal(hand) > limit;
    }
}
