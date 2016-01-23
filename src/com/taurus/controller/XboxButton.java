package com.taurus.controller;

import edu.wpi.first.wpilibj.buttons.Button;

public class XboxButton extends Button
{
    Xbox hardware;
    int index;  // TODO Make generic
    
    public XboxButton(Xbox hardware) {
        hardware = this.hardware;
    }

    public boolean get()
    {
        return hardware.getAButton();  // TODO Make generic
    }
}
