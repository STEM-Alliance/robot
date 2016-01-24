package com.taurus.controller;

import edu.wpi.first.wpilibj.buttons.Button;
/**
 * Xbox button for use with Command based robot
 * @author drlindne
 *
 */
public class XboxButton extends Button
{
    Xbox hardware;
    Xbox.ButtonType button;
    
    public XboxButton(Xbox hardware, Xbox.ButtonType button) {
        hardware = this.hardware;
    }

    public boolean get()
    {
        return hardware.getButton(button);
    }
}
