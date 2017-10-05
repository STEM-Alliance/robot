package org.wfrobotics.prototype.config;

import org.wfrobotics.prototype.commands.ExampleForwardCommand;
import org.wfrobotics.reuse.controller.Xbox;
import org.wfrobotics.reuse.controller.XboxButton;

import edu.wpi.first.wpilibj.buttons.Button;

/** Maps Buttons to Commands **/
public class IO
{
    public static Xbox controller = new Xbox(0);

    public static Button X = new XboxButton (controller, Xbox.BUTTON.X);
    public static Button Y = new XboxButton (controller, Xbox.BUTTON.Y);

    // TODO create more buttons if you need them to do your testing

    public IO()
    {
        X.whileHeld(new ExampleForwardCommand());
        X.whileHeld(new ExampleForwardCommand());  // TODO Switch the 'Y' Button to run another Command, such as ExampleBackwardCommand

        // TODO tell any additional buttons when to run, what Command
    }
}