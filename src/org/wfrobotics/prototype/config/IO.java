package org.wfrobotics.prototype.config;

import org.wfrobotics.prototype.commands.DrivePath;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.controller.Xbox;

import edu.wpi.first.wpilibj.buttons.Button;

/** Maps Buttons to Commands **/
public class IO
{
    public static Xbox controller = new Xbox(0);

    public static Button X = ButtonFactory.makeButton(controller, Xbox.BUTTON.X, TRIGGER.WHEN_PRESSED, new DrivePath());
    public static Button Y = ButtonFactory.makeButton(controller, Xbox.BUTTON.Y, TRIGGER.WHEN_PRESSED, new DrivePath());
    // TODO Switch the 'Y' Button to run another Command, such as ExampleBackwardCommand
    // TODO create more buttons if you need them to do your testing
}