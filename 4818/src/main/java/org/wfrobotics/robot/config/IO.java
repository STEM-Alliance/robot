package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.commands.SignalHumanPlayer;
import org.wfrobotics.reuse.commands.drive.TurnToHeading;
import org.wfrobotics.reuse.commands.drive.TurnToTarget;
import org.wfrobotics.reuse.config.AutoFactory;
import org.wfrobotics.reuse.config.ButtonFactory;
import org.wfrobotics.reuse.config.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.config.EnhancedIO;
import org.wfrobotics.reuse.config.HerdJoystick;
import org.wfrobotics.reuse.config.Xbox;
import org.wfrobotics.reuse.config.Xbox.DPAD;
import org.wfrobotics.robot.commands.AutoLiftToScale;
import org.wfrobotics.robot.commands.DriveToTarget;
import org.wfrobotics.robot.commands.intake.IntakeManual;
import org.wfrobotics.robot.commands.intake.JawsToggle;
import org.wfrobotics.robot.commands.intake.SmartOutake;
import org.wfrobotics.robot.commands.VisionScore;

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
        // THUMB_TOP_RIGHT(1),
        // THUMB_BOTTOM_RIGHT(2),
        // THUMB_TOP_LEFT(3),
        // THUMB_BOTTOM_LEFT(4),
        // TRIGGER(5),
        // THUMB_SIDE(6),
        // BASE_TOP_RIGHT(7),
        // BASE_MIDDLE_RIGHT(8),
        // BASE_BOTTOM_RIGHT(9),
        // BASE_TOP_LEFT(10),
        // BASE_MIDDLE_LEFT(11),
        // BASE_BOTTOM_LEFT(12);
        ButtonFactory.makeButton(driverThrottle, HerdJoystick.BUTTON.BUTTON7, TRIGGER.WHEN_PRESSED, AutoFactory.getInstance().makeCommand(Auto.modes));
        ButtonFactory.makeButton(driverThrottle, HerdJoystick.BUTTON.BUTTON8, TRIGGER.WHEN_PRESSED, AutoFactory.getInstance().makeCommand(Auto.delays));
        ButtonFactory.makeButton(driverThrottle, HerdJoystick.BUTTON.BUTTON9, TRIGGER.WHEN_PRESSED, AutoFactory.getInstance().makeCommand(Auto.positions));

        // ------------------------- Drive ------------------------
        ButtonFactory.makeButton(driverThrottle, HerdJoystick.BUTTON.BUTTON1, TRIGGER.WHILE_HELD, new DriveToTarget());
        ButtonFactory.makeButton(driverThrottle, HerdJoystick.BUTTON.BUTTON3, TRIGGER.WHILE_HELD, new TurnToTarget());

        // ------------------------ Intake ------------------------

        ButtonFactory.makeButton(operator, Xbox.AXIS.RIGHT_TRIGGER, .1, TRIGGER.WHILE_HELD, new IntakeManual());
        ButtonFactory.makeButton(operator, Xbox.AXIS.LEFT_TRIGGER, .1, TRIGGER.WHILE_HELD, new IntakeManual());
        ButtonFactory.makeButton(operator, Xbox.BUTTON.LB, TRIGGER.WHEN_PRESSED, new JawsToggle());
        ButtonFactory.makeButton(operator, Xbox.BUTTON.B, TRIGGER.WHEN_PRESSED, new SmartOutake());

        // ------------------------- Lift -------------------------

        ButtonFactory.makeButton(operator, Xbox.BUTTON.Y, TRIGGER.WHEN_PRESSED, new AutoLiftToScale());
        ButtonFactory.makeButton(operator, Xbox.BUTTON.A, TRIGGER.WHEN_PRESSED, new TurnToHeading(90));
        ButtonFactory.makeButton(operator, Xbox.BUTTON.X, TRIGGER.WHEN_PRESSED, new TurnToTarget());

        // -------------------- Super Structure -------------------

        ButtonFactory.makeButton(operator, Xbox.DPAD.RIGHT, TRIGGER.WHILE_HELD, new SignalHumanPlayer());

        // ------------------------ Debug -------------------------

        //        ButtonFactory.makeButton(operator, Xbox.BUTTON.START, TRIGGER.WHEN_PRESSED, new ModeTestPathVelocity());
        //        ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new DrivePathTest(17.0 * 12.0, 0.0 * 12.0));
        ButtonFactory.makeButton(operator, Xbox.BUTTON.START, TRIGGER.WHEN_PRESSED, new VisionScore());
        //        ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new ModeOppisitScalse());
        ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new TurnToHeading(90.0));
        //        ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new TuningTrajectory());
        //        ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.TOGGLE_WHEN_PRESSED, new BlinkInArea(60.0, 36.0, 36.0, 60.0));
        //        ButtonFactory.makeButton(operator, Xbox.BUTTON.START, TRIGGER.WHEN_PRESSED, new CharacterizeDrivetrain());
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
        return operator.getAxis(Xbox.AXIS.RIGHT_Y);
    }

    public double getWinchPercent()
    {
        int val = operator.getDpad();
        double speed = 0.0;

        if (val == DPAD.UP.get() || val == DPAD.UP_LEFT.get() || val == DPAD.UP_RIGHT.get())
        {
            speed = 1.0;
        }
        else if (val == DPAD.DOWN.get() || val == DPAD.DOWN_LEFT.get() || val == DPAD.DOWN_RIGHT.get())
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

    public boolean isDriveOverrideRequested() 
    {
		return false;
    }
    
    public void setRumble(boolean rumble)
    {
        float state = (rumble) ? 1 : 0;
        operator.setRumble(Hand.kLeft, state);
        operator.setRumble(Hand.kRight, state);
    }
}