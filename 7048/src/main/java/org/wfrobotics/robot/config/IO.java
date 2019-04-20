package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.ButtonFactory;
import org.wfrobotics.reuse.config.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.config.EnhancedIO;
import org.wfrobotics.reuse.config.HerdJoystick;
import org.wfrobotics.reuse.config.Xbox;
import org.wfrobotics.reuse.commands.drive.FollowTarget;
import org.wfrobotics.robot.commands.wrist.WristPneumaticToggle;
import org.wfrobotics.robot.commands.intake.ToggleHatch;
import org.wfrobotics.robot.commands.climb.ClimbTogglePneumatic;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;

/** Maps controllers to Commands **/
public final class IO implements EnhancedIO
{
    private static IO instance = null;
    private final HerdJoystick driverThrottle;
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
        //Hat down
        ButtonFactory.makeButton(driverThrottle, 1, TRIGGER.WHEN_PRESSED, new FollowTarget());

        //----------------------- Intake --------------------------
        ButtonFactory.makeButton(operator, Xbox.BUTTON.X, TRIGGER.WHEN_PRESSED, new ToggleHatch());

        //----------------------- Wrist --------------------------
        ButtonFactory.makeButton(operator, Xbox.BUTTON.Y, TRIGGER.WHEN_PRESSED, new WristPneumaticToggle());

        //-----------------------  --------------------------
        ButtonFactory.makeButton(operator, Xbox.BUTTON.LB, TRIGGER.WHEN_PRESSED, new ClimbTogglePneumatic());
    }

    // ------------------- Robot-specific --------------------

    public double getElevatorStick()
    {
        return operator.getY(Hand.kLeft);
    }

    public double getIntakeIn()
    {
        return operator.getTrigger(Hand.kLeft);
    }

    public double getIntakeOut()
    {
        return operator.getTrigger(Hand.kRight);
    }

    public double getWristStick()
    {
        return operator.getY(Hand.kRight);
    }

    public boolean isElevatorOverrideRequested()
    {
        return  Math.abs(getElevatorStick()) > .15;
    }

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
        return driverThrottle.getY();
    }

    public double getTurn()
    {
        return driverTurn.getRawAxis(0)*0.75;
    }

    public boolean getDriveQuickTurn()
    {
        //Turn joystick trigger
        return driverTurn.getRawButtonPressed(0);
    }

    public boolean isDriveOverrideRequested()
    {
        return Math.abs(getThrottle()) > 0.15 || Math.abs(getTurn()) > 0.15;
    }

    public void setRumble(boolean rumble)
    {
        float state = (rumble) ? 1 : 0;
        operator.setRumble(Hand.kLeft, state);
        operator.setRumble(Hand.kRight, state);
    }
}
