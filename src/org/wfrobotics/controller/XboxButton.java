package org.wfrobotics.controller;

import edu.wpi.first.wpilibj.buttons.Button;
/**
 * Xbox button for use with Command based robot
 * @author drlindne
 *
 */
public class XboxButton extends Button
{
    Xbox hardware;
    Xbox.BUTTON button;
    
    public XboxButton(Xbox hardware, Xbox.BUTTON button) {
        this.hardware = hardware;
        this.button = button;
    }

    public boolean get()
    {
        return hardware.getButton(button);
    }
}
