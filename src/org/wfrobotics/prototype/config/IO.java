package org.wfrobotics.prototype.config;

import org.wfrobotics.reuse.controller.Xbox;

/** Maps Buttons to Commands **/
public class IO
{
    public static Xbox controller = new Xbox(0);
    public static Xbox man = new Xbox(1);
    //
    // public static Button manB = ButtonFactory.makeButton
    // (man, Xbox.BUTTON.B, TRIGGER.WHILE_HELD, new IntakePush(.5));

}