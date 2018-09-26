package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.commands.SignalHumanPlayer;
import org.wfrobotics.reuse.commands.drive.TurnToHeading;
import org.wfrobotics.reuse.config.ButtonFactory;
import org.wfrobotics.reuse.config.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.config.HerdJoystick;
import org.wfrobotics.reuse.config.Xbox;
import org.wfrobotics.reuse.config.Xbox.DPAD;
import org.wfrobotics.robot.auto.ModeScale;
import org.wfrobotics.robot.commands.AutoLiftToBottom;
import org.wfrobotics.robot.commands.AutoLiftToScale;
import org.wfrobotics.robot.commands.intake.IntakeManual;
import org.wfrobotics.robot.commands.intake.JawsToggle;
import org.wfrobotics.robot.commands.intake.SmartOutake;
import org.wfrobotics.robot.commands.lift.LiftGoHome;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;

/** Maps controllers to Commands **/
public final class IO
{
    private static IO instance = null;
    private final HerdJoystick driverThrottle;  // TODO Refactor - Make Button from JoyStick instead?
    private final Joystick driverTurn;
    private final Xbox operator;

    private IO(HerdJoystick driverThrottle, Joystick driverTurn, Xbox operator)
    {
        this.driverThrottle = driverThrottle;
        this.driverTurn = driverTurn;
        this.operator = operator;

        // ---------------------- Autonomous ----------------------
        ButtonFactory.makeButton(driverThrottle, HerdJoystick.BUTTON.BASE_TOP_RIGHT, TRIGGER.WHEN_PRESSED, Auto.factory.makeCommand(Auto.modes));
        ButtonFactory.makeButton(driverThrottle, HerdJoystick.BUTTON.BASE_MIDDLE_RIGHT, TRIGGER.WHEN_PRESSED, Auto.factory.makeCommand(Auto.delays));
        ButtonFactory.makeButton(driverThrottle, HerdJoystick.BUTTON.BASE_BOTTOM_RIGHT, TRIGGER.WHEN_PRESSED, Auto.factory.makeCommand(Auto.positions));

        // ------------------------- Drive ------------------------

        // ------------------------ Intake ------------------------

        ButtonFactory.makeButton(operator, Xbox.AXIS.RIGHT_TRIGGER, .1, TRIGGER.WHILE_HELD, new IntakeManual());
        ButtonFactory.makeButton(operator, Xbox.AXIS.LEFT_TRIGGER, .1, TRIGGER.WHILE_HELD, new IntakeManual());
        ButtonFactory.makeButton(operator, Xbox.BUTTON.LB, TRIGGER.WHEN_PRESSED, new JawsToggle());
        ButtonFactory.makeButton(operator, Xbox.BUTTON.B, TRIGGER.WHEN_PRESSED, new SmartOutake());

        // ------------------------- Lift -------------------------

        ButtonFactory.makeButton(operator, Xbox.BUTTON.Y, TRIGGER.WHEN_PRESSED, new AutoLiftToScale());
        ButtonFactory.makeButton(operator, Xbox.BUTTON.A, TRIGGER.WHEN_PRESSED, new AutoLiftToBottom());
        ButtonFactory.makeButton(operator, Xbox.BUTTON.X, TRIGGER.WHEN_PRESSED, new LiftGoHome(-.3, 10));

        // -------------------- Super Structure -------------------

        ButtonFactory.makeButton(operator, Xbox.DPAD.RIGHT, TRIGGER.WHILE_HELD, new SignalHumanPlayer());

        // ------------------------ Debug -------------------------

        //        ButtonFactory.makeButton(operator, Xbox.BUTTON.START, TRIGGER.WHEN_PRESSED, new ModeTestPathVelocity());
        //        ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new DrivePathTest(17.0 * 12.0, 0.0 * 12.0));
        ButtonFactory.makeButton(operator, Xbox.BUTTON.START, TRIGGER.WHEN_PRESSED, new ModeScale());
        //        ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new ModeOppisitScalse());
        ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new TurnToHeading(130.0));
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
            instance = new IO(new HerdJoystick(0), new Joystick(1), new Xbox(2));
        }
        return instance;
    }

    public double getThrottle()
    {
        return -driverThrottle.getY();
    }

    public double getTurn()
    {
        final double val = driverTurn.getRawAxis(0);
        return Math.signum(val) * Math.pow(val, 2);  // TODO Remove?
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
}