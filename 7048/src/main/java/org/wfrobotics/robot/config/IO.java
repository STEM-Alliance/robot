package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.AutoFactory;
import org.wfrobotics.reuse.config.ButtonFactory;
import org.wfrobotics.reuse.config.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.config.EnhancedIO;
import org.wfrobotics.reuse.config.HerdJoystick;
import org.wfrobotics.reuse.config.Xbox;
import org.wfrobotics.reuse.commands.drive.DriveToTarget;
import org.wfrobotics.robot.commands.intake.PopHatch;
import org.wfrobotics.robot.commands.wrist.WristToHeight;

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
        ButtonFactory.makeButton(driverThrottle, HerdJoystick.BUTTON.BASE_TOP_RIGHT, TRIGGER.WHEN_PRESSED, AutoFactory.getInstance().makeCommand(Auto.modes));
        ButtonFactory.makeButton(driverThrottle, HerdJoystick.BUTTON.BASE_MIDDLE_RIGHT, TRIGGER.WHEN_PRESSED, AutoFactory.getInstance().makeCommand(Auto.delays));

        ButtonFactory.makeButton(driverThrottle, HerdJoystick.BUTTON.THUMB_BOTTOM_LEFT, TRIGGER.WHEN_PRESSED, new DriveToTarget());

        //----------------------- Elevator ------------------------
        //ButtonFactory.makeButton(operator, Xbox.BUTTON.Y, TRIGGER.WHEN_PRESSED, new ElevatorToHeight(ArmHeight.HatchHigh.get()));
        //ButtonFactory.makeButton(operator, Xbox.BUTTON.B, TRIGGER.WHEN_PRESSED, new ElevatorToHeight(ArmHeight.HatchMiddle.get()));
        //ButtonFactory.makeButton(operator, Xbox.BUTTON.A, TRIGGER.WHEN_PRESSED, new ElevatorToHeight(ArmHeight.HatchLow.get()));

        //----------------------- Intake --------------------------
        ButtonFactory.makeButton(operator, Xbox.BUTTON.X, TRIGGER.WHEN_PRESSED, new PopHatch());

        //----------------------- System --------------------------

        //----------------------- Wrist ---------------------------
        ButtonFactory.makeButton(operator, Xbox.BUTTON.Y, TRIGGER.WHEN_PRESSED, new WristToHeight(30));

        //ButtonFactory.makeButton(operator, Xbox.BUTTON.RB, TRIGGER.WHEN_PRESSED, new WristToggle());

        //----------------------- Testing -------------------------
        //ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new WristToHeight(45.0));


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
        return driverTurn.getRawAxis(0)*0.25;
    }

    public boolean getDriveQuickTurn()
    {
        return Math.abs(getThrottle()) < 0.1;
        //return true;
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