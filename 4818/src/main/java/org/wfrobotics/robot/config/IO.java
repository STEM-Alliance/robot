package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.AutoFactory;
import org.wfrobotics.reuse.config.ButtonFactory;
import org.wfrobotics.reuse.config.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.config.Xbox.AXIS;
import org.wfrobotics.robot.commands.CommandTemplate;
import org.wfrobotics.robot.commands.ControlDirection;
import org.wfrobotics.robot.commands.MoveBottom;
import org.wfrobotics.robot.commands.MoveTop;
import org.wfrobotics.reuse.config.HerdJoystick;
import org.wfrobotics.reuse.config.EnhancedIO;
import org.wfrobotics.reuse.config.Xbox;


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

        ButtonFactory.makeButton(operator, Xbox.BUTTON.RB, TRIGGER.TOGGLE_WHEN_PRESSED, new CommandTemplate());
        ButtonFactory.makeButton(operator, Xbox.BUTTON.A, TRIGGER.WHEN_PRESSED, new MoveTop(1.0));
        ButtonFactory.makeButton(operator, Xbox.BUTTON.X, TRIGGER.WHILE_HELD, new ControlDirection());
        

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
    public double getThrottle(){
        return 0.0;
    }


    public double getReach()
    {
        double pos = (operator.getY(Hand.kLeft));
        return pos;

    }

    public double getBase()
    {
        double pos = -operator.getX(Hand.kRight);
        
        return pos;
    }

    public double getLift()
    {
        double pos = -operator.getY(Hand.kRight);
        
        return pos;
    }

    public double getClaw(){
        double posR = operator.getTrigger(Hand.kRight);
        double posL = -operator.getTrigger(Hand.kLeft);

        if (posR > -posL){
            return posR;
        }
        return posL;
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