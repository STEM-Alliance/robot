package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.commands.SignalHumanPlayer;
import org.wfrobotics.reuse.config.ButtonFactory;
import org.wfrobotics.reuse.config.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.config.HerdJoystick;
import org.wfrobotics.reuse.config.Xbox;
import org.wfrobotics.reuse.config.EnhancedIO;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;

/** Maps controllers to Commands **/
public final class IO implements EnhancedIO
{
    private static IO instance = null;
    private final HerdJoystick driverThrottle;  // TODO Refactor - Make Button from JoyStick instead?
    private final Joystick driverTurn;
    private final Xbox operator;

    /** Create and configure controls for Drive Team */
    private IO()
    {
        driverThrottle = new HerdJoystick(0);
        driverTurn = new Joystick(1);
        operator = new Xbox(2);
    }

    /** Configure each Button to run a Command */
    public void assignButtons()
    {
        // ---------------------- Autonomous ----------------------



        //----------------------Testing-------------------------------

        // -------------------- Super Structure -------------------

        ButtonFactory.makeButton(operator, Xbox.DPAD.RIGHT, TRIGGER.WHILE_HELD, new SignalHumanPlayer());

    }

    // ------------------- Robot-specific --------------------



    //    public double getWristStick()
    //    {
    //        return operator.getAxis(Xbox.AXIS.LEFT_Y);
    //    }


    // ------------------------ Reuse ------------------------

    public static IO getInstance()
    {
        if (instance == null)
        {
            instance = new IO();
        }
        return instance;
    }

    public double getThrottle()
    {
        return -driverThrottle.getY();
    }

    public double getTurn()
    {
        return driverTurn.getRawAxis(0);
    }

    public boolean getDriveQuickTurn()
    {
        return Math.abs(getThrottle()) < 0.1;
    }

    public void setRumble(boolean rumble)
    {
        float state = (rumble) ? 1 : 0;
        operator.setRumble(Hand.kLeft, state);
        operator.setRumble(Hand.kRight, state);
    }

	@Override
	public boolean isDriveOverrideRequested() {
		// TODO Auto-generated method stub
		return false;
	}
}