package org.wfrobotics.reuse.controller;

import edu.wpi.first.wpilibj.buttons.Button;
/**
 * Xbox Dpad button for use with Command based robot
 * @author bpoppe
 *
 */
public class XboxDpadButton extends Button
{
    Xbox hardware;
    int angle;
    
    public XboxDpadButton(Xbox hardware, int angle) {
        this.hardware = hardware;
        this.angle = angle;
    }
    
    public boolean get()
    {
        return hardware.getPOV(0) == angle;
    }
}
