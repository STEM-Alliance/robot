package org.wfrobotics.reuse.controller;

import org.wfrobotics.reuse.controller.Xbox.AXIS;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;
/**
 * Xbox trigger button, uses trigger as button source
 * @author bpoppe
 *
 */
public class XboxTriggerButton extends Button
{
    private final Xbox hardware;
    private final AXIS a;
    private final double limit;

    /**
     * Constructor, Button with XBox trigger as source
     * @param hardware Which XBox Controller
     * @param hand Side of the controller this trigger is on
     * @param thresholdOn Percentage pressed that counts as the button being pressed (range: 0 - 1)
     */
    public XboxTriggerButton(Xbox hardware, Hand hand, double thresholdOn)
    {
        this.hardware = hardware;
        a = (hand == Hand.kLeft) ? AXIS.LEFT_TRIGGER : AXIS.RIGHT_TRIGGER;
        limit = thresholdOn;
    }

    public boolean get()
    {
        return hardware.getAxis(a) > limit;
    }
}
