package org.wfrobotics.robot.config;

import org.wfrobotics.robot.commands.ExampleForwardCommand;
import org.wfrobotics.reuse.config.ButtonFactory;
import org.wfrobotics.reuse.config.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.config.Xbox;
import org.wfrobotics.reuse.config.IOConfig;

import edu.wpi.first.wpilibj.buttons.Button;

/** Maps Buttons to Commands **/
public class ProtoIO implements IOConfig
{
    public static Xbox controller;

    /** Create and configure controls for Drive Team */
    public ProtoIO()
    {
        controller = new Xbox(0);
    }

    /** Configure each Button to run a Command */
    public void assignButtons()
    {

        Button X = ButtonFactory.makeButton(controller, Xbox.BUTTON.X, TRIGGER.WHILE_HELD, new ExampleForwardCommand());
        Button Y = ButtonFactory.makeButton(controller, Xbox.BUTTON.Y, TRIGGER.WHILE_HELD, new ExampleForwardCommand());
        // TODO Switch the 'Y' Button to run another Command, such as ExampleBackwardCommand
        // TODO create more buttons if you need them to do your testing
    }

    public void setRumble(boolean b)
    {

    }

    public boolean getDriveQuickTurn()
    {
        return false;
    }
    
    public double getThrottle()
    {
        return 0;
    }

    public double getTurn()
    {
        return 0;
    }

	@Override
	public boolean isDriveOverrideRequested() {
		// TODO Auto-generated method stub
		return false;
	}

}