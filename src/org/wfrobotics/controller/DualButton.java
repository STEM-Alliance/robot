package org.wfrobotics.controller;

import edu.wpi.first.wpilibj.buttons.Button;
/**
 * Two buttons for use with Command based robot
 * @author bpoppe
 *
 */
public class DualButton extends Button
{
    Button button1;
    Button button2;
    
    public DualButton(Button button1, Button button2) {
        this.button1 = button2;
        this.button2 = button2;
    }

    public boolean get()
    {
        return button1.get() && button2.get();
    }
}
