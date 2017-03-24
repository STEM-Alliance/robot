package org.wfrobotics.controller;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;

public class XboxJoystickButton extends Button 
{
    private final Xbox xbox;
    private final Hand hand;
    
    public XboxJoystickButton(Xbox hardware, Hand hand)
    {
        this.xbox = hardware;
        this.hand = hand;
    }

    @Override
    public boolean get()
    {
        return xbox.getJoystickButton(hand);
    }
}
