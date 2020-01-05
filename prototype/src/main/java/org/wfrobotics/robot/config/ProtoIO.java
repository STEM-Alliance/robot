package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.Xbox;
import org.wfrobotics.reuse.config.ButtonFactory.TRIGGER;
import org.wfrobotics.robot.commands.ExampleForwardCommand;
import org.wfrobotics.reuse.config.ButtonFactory;
import org.wfrobotics.reuse.config.EnhancedIO;

/** Maps Buttons to Commands **/
public class ProtoIO implements EnhancedIO
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
        // TODO Switch the 'Y' Button to run another Command, such as ExampleBackwardCommand
        // TODO create more buttons if you need them to do your testing
        ButtonFactory.makeButton(controller, Xbox.BUTTON.X, TRIGGER.WHEN_PRESSED, new ExampleForwardCommand(1.0));

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