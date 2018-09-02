package org.wfrobotics.robot.config;

import java.util.ArrayList;

import org.wfrobotics.reuse.commands.SignalHumanPlayer;
import org.wfrobotics.reuse.commands.drive.TurnToHeading;
import org.wfrobotics.reuse.config.ButtonFactory;
import org.wfrobotics.reuse.config.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.config.Xbox;
import org.wfrobotics.reuse.config.Xbox.AXIS;
import org.wfrobotics.reuse.config.Xbox.DPAD;
import org.wfrobotics.reuse.utilities.Testable;
import org.wfrobotics.robot.auto.modes.ModeScale2;
import org.wfrobotics.robot.commands.AutoLiftToBottom;
import org.wfrobotics.robot.commands.AutoLiftToScale;
import org.wfrobotics.robot.commands.intake.IntakeManual;
import org.wfrobotics.robot.commands.intake.JawsToggle;
import org.wfrobotics.robot.commands.intake.SmartOutake;
import org.wfrobotics.robot.commands.lift.LiftGoHome;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;

/** Maps controllers to Commands **/
public final class IO implements Testable
{
    private static IO instance = null;
    private final ArrayList<Button> buttons = new ArrayList<Button>();  // Keep buttons instantiated
    private final Joystick driverThrottle;
    private final Joystick driverTurn;
    private final Xbox operator;

    private IO(Joystick driverThrottle, Joystick driverTurn, Xbox operator)
    {
        this.driverThrottle = driverThrottle;
        this.driverTurn = driverTurn;
        this.operator = operator;

        // ------------------------- Drive ------------------------

        // ------------------------ Intake ------------------------

        buttons.add(ButtonFactory.makeButton(operator, Xbox.AXIS.RIGHT_TRIGGER, .1, TRIGGER.WHILE_HELD, new IntakeManual()));
        buttons.add(ButtonFactory.makeButton(operator, Xbox.AXIS.LEFT_TRIGGER, .1, TRIGGER.WHILE_HELD, new IntakeManual()));
        buttons.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.LB, TRIGGER.WHEN_PRESSED, new JawsToggle()));
        buttons.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.B, TRIGGER.WHEN_PRESSED, new SmartOutake()));

        // ------------------------- Lift -------------------------

        buttons.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.Y, TRIGGER.WHEN_PRESSED, new AutoLiftToScale()));
        buttons.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.A, TRIGGER.WHEN_PRESSED, new AutoLiftToBottom()));
        buttons.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.X, TRIGGER.WHEN_PRESSED, new LiftGoHome(-.3, 10)));

        // -------------------- Super Structure -------------------

        buttons.add(ButtonFactory.makeButton(operator, Xbox.DPAD.RIGHT, TRIGGER.WHILE_HELD, new SignalHumanPlayer()));

        // ------------------------ Debug -------------------------

        //        buttons.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.START, TRIGGER.WHEN_PRESSED, new ModeTestPathVelocity()));
        //        buttons.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new DrivePathTest(17.0 * 12.0, 0.0 * 12.0)));
        buttons.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.START, TRIGGER.WHEN_PRESSED, new ModeScale2()));
        //        buttons.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new TeleopPath(new StartToScaleR(), 4.0)));
        buttons.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new TurnToHeading(-135.0, 1.0)));
        //        buttons.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.TOGGLE_WHEN_PRESSED, new BlinkInArea(60.0, 36.0, 36.0, 60.0)));
    }

    // ------------------- Robot-specific --------------------

    public double getIntakeIn()
    {

        return operator.getTrigger(Hand.kRight);
    }

    public double getIntakeOut()
    {
        return operator.getTrigger(Hand.kLeft);
    }

    public double getWristStick()
    {
        return operator.getAxis(Xbox.AXIS.LEFT_Y);
    }

    public double getLiftStick()
    {
        return operator.getAxis(AXIS.RIGHT_Y);
    }

    public double getWinchPercent()
    {
        int direction = operator.getDpad();
        double speed = 0.0;

        if (direction == DPAD.UP.get() || direction == DPAD.UP_LEFT.get() || direction == DPAD.UP_RIGHT.get())
        {
            speed = 1.0;
        }
        else if (direction == DPAD.DOWN.get() || direction == DPAD.DOWN_LEFT.get() || direction == DPAD.DOWN_RIGHT.get())
        {
            speed = -1.0;
        }

        return speed;
    }

    public boolean isLiftOverrideRequested()
    {
        return  Math.abs(getLiftStick()) > .15;
    }

    // ------------------------ Reuse ------------------------

    public static IO getInstance()
    {
        if (instance == null)
        {
            instance = new IO(new Joystick(0), new Joystick(1), new Xbox(2));
        }
        return instance;
    }

    public double getThrottle()
    {
        return -driverThrottle.getY();
    }

    public double getTurn()
    {
        return Math.signum(driverTurn.getRawAxis(0)) * Math.pow(driverTurn.getRawAxis(0), 2);
    }

    public boolean getDriveQuickTurn()
    {
        return driverThrottle.getY() < 0.1;
    }

    public void setRumble(boolean rumble)
    {
        float state = (rumble) ? 1 : 0;
        operator.setRumble(Hand.kLeft, state);
        operator.setRumble(Hand.kRight, state);
    }

    public boolean runFunctionalTest(boolean includeMotion)
    {
        // TODO Refactor button adding, then check if inputs contain unique targeted buttons

        return false;
    }
}