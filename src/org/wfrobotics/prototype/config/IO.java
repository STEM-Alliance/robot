package org.wfrobotics.prototype.config;

import org.wfrobotics.reuse.commands.drivebasic.DriveDistance;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.controller.Xbox;
import org.wfrobotics.reuse.controller.Xbox.BUTTON;

import edu.wpi.first.wpilibj.buttons.Button;

/** Maps Buttons to Commands **/
public class IO
{
    public static Xbox controller = new Xbox(0);
    //
    // public static Button manB = ButtonFactory.makeButton
    // (man, Xbox.BUTTON.B, TRIGGER.WHILE_HELD, new IntakePush(.5));
    public static Button driveDistanceButton = ButtonFactory.makeButton(controller, BUTTON.A, TRIGGER.WHEN_PRESSED, new DriveDistance(6));
}