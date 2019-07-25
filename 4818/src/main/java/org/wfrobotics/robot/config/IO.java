package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.AutoFactory;
import org.wfrobotics.reuse.config.ButtonFactory;
import org.wfrobotics.reuse.config.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.config.Xbox.AXIS;
import org.wfrobotics.reuse.config.HerdJoystick;
import org.wfrobotics.reuse.config.EnhancedIO;
import org.wfrobotics.reuse.config.Xbox;

import org.wfrobotics.robot.commands.Move;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;

/** Maps controllers to Commands **/
public final class IO implements EnhancedIO
{
    private static IO instance = null;
    private final Xbox operator;

    /** Create and configure controls for Drive Team */
    private IO()
    {
        operator = new Xbox(0);
    }

    /** Configure each Button to run a Command */
    public void assignButtons()
    {
        // ---------------------- Autonomous ----------------------
        // ButtonFactory.makeButton(driverThrottle, 7, TRIGGER.WHEN_PRESSED, AutoFactory.getInstance().makeCommand(Auto.modes));
        // ButtonFactory.makeButton(driverThrottle, 8, TRIGGER.WHEN_PRESSED, AutoFactory.getInstance().makeCommand(Auto.delays));
        // ButtonFactory.makeButton(driverThrottle, 9, TRIGGER.WHEN_PRESSED, AutoFactory.getInstance().makeCommand(Auto.positions));

        ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new Move(0.0));
        ButtonFactory.makeButton(operator, Xbox.BUTTON.A, TRIGGER.WHEN_PRESSED, new Move(0.25));
        ButtonFactory.makeButton(operator, Xbox.BUTTON.B, TRIGGER.WHEN_PRESSED, new Move(0.5));
        ButtonFactory.makeButton(operator, Xbox.BUTTON.X, TRIGGER.WHEN_PRESSED, new Move(0.75));
        ButtonFactory.makeButton(operator, Xbox.BUTTON.Y, TRIGGER.WHEN_PRESSED, new Move(1.0));

    }

    // ------------------- Robot-specific --------------------

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
        return -operator.getAxis(AXIS.RIGHT_TRIGGER);
    }

    public double getTurn()
    {
        return 0.0;
    }

    public boolean getDriveQuickTurn()
    {
        return false;
    }

    public boolean isDriveOverrideRequested()
    {
        return false;
    }

    public void setRumble(boolean rumble)
    {
        double state = (rumble) ? 1.0 : 0.0;
        operator.setRumble(Hand.kLeft, state);
        operator.setRumble(Hand.kRight, state);
    }
}