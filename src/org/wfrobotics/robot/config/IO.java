package org.wfrobotics.robot.config;

import java.util.ArrayList;

import org.wfrobotics.reuse.commands.drivebasic.TurnToHeading;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.controller.Panel;
import org.wfrobotics.reuse.controller.Xbox;
import org.wfrobotics.reuse.controller.Xbox.AXIS;
import org.wfrobotics.reuse.controller.Xbox.DPAD;
import org.wfrobotics.reuse.driveio.Arcade.ArcadeIO;
import org.wfrobotics.reuse.driveio.Arcade.ArcadeRocketJoyStick;
import org.wfrobotics.reuse.driveio.Mecanum.MecanumIO;
import org.wfrobotics.reuse.driveio.Swerve.SwerveIO;
import org.wfrobotics.reuse.driveio.Tank.TankIO;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.commands.intake.IntakeManual;
import org.wfrobotics.robot.commands.intake.JawsToggle;
import org.wfrobotics.robot.commands.intake.SmartOutake;
import org.wfrobotics.robot.commands.lift.LiftGoHome;
import org.wfrobotics.robot.commands.lift.LiftToHeight;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;

/** Maps controllers to Commands **/
public class IO
{
    private static IO instance = null;
    private final ArrayList<Button> robotSpecific = new ArrayList<Button>();  // Keep buttons instantiated
    private final Joystick driverThrottle;
    private final Joystick driverTurn;
    private final Xbox operator;
    private final Panel panel;

    /* Only one of these should be instantiated at a time */
    public TankIO tankIO;
    public ArcadeIO arcadeIO;
    public MecanumIO mecanumIO;
    public SwerveIO swerveIO;

    private IO(Joystick driverThrottle, Joystick driverTurn, Xbox operator, Panel panel)
    {
        this.driverThrottle = driverThrottle;
        this.driverTurn = driverTurn;
        this.operator = operator;
        this.panel = panel;

        // ------------------- Select Drive-style  ----------------
        arcadeIO = new ArcadeRocketJoyStick(driverThrottle, driverTurn);

        // ------------------------- Drive ------------------------
        //        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.BUTTON.START, TRIGGER.WHEN_PRESSED, new ShiftToggle()));
        //        //        robotSpecific.add(ButtonFactory.makeButton(panel, Panel.BUTTON.YELLOW_T, TRIGGER.TOGGLE_WHEN_PRESSED, new DriveOff()));
        //
        //        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.DPAD.UP, TRIGGER.WHEN_PRESSED, new TurnToHeading(0, 2)));
        //        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.DPAD.RIGHT, TRIGGER.WHEN_PRESSED, new TurnToHeading(90, 2)));
        //        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.DPAD.LEFT, TRIGGER.WHEN_PRESSED, new TurnToHeading(-90, 2)));
        //        robotSpecific.add(ButtonFactory.makeButton(driver, Xbox.DPAD.DOWN, TRIGGER.WHEN_PRESSED, new TurnToHeading(180, 2)));

        // ------------------------ Intake ------------------------
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.AXIS.RIGHT_TRIGGER, .1, TRIGGER.WHILE_HELD, new IntakeManual()));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.AXIS.LEFT_TRIGGER, .1, TRIGGER.WHILE_HELD, new IntakeManual()));
        //        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.AXIS.LEFT_Y, .1, -.1, TRIGGER.WHILE_HELD, new IntakeLiftAutoZeroThenPercentVoltage()));
        //        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.LEFT_STICK, TRIGGER.WHEN_PRESSED, new IntakeLiftZero()));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.LB, TRIGGER.WHEN_PRESSED, new JawsToggle()));
        //        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.DPAD.RIGHT, TRIGGER.WHEN_PRESSED, new IntakeLiftToHeight(.75)));
        //        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.DPAD.LEFT, TRIGGER.WHEN_PRESSED, new IntakeLiftToHeight(.25)));
        // ------------------------- Lift -------------------------
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.Y, TRIGGER.WHEN_PRESSED, new LiftToHeight(LiftHeight.Scale.get())));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.B, TRIGGER.WHEN_PRESSED, new SmartOutake()));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.A, TRIGGER.WHEN_PRESSED, new LiftToHeight(LiftHeight.Intake.get())));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.X, TRIGGER.WHEN_PRESSED, new LiftGoHome(-.3, 10)));

        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.START, TRIGGER.WHEN_PRESSED, new TurnToHeading(0.0 , 2.0)));
        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new TurnToHeading(90.0 , 2.0)));
        //        robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.START, TRIGGER.WHEN_PRESSED, new PickLocation()));
    }

    // ------------------- Robot-specific --------------------

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
        return driverTurn.getRawButton(1);
    }

    public double getIntakeIn()
    {
        double value = operator.getTrigger(Hand.kLeft);
        return (value > .1) ? value : 0;
    }

    public double getIntakeOut()
    {
        double value = operator.getTrigger(Hand.kRight);
        return (value > .1) ? value : 0;
    }

    public double getIntakeLift()
    {
        double value = operator.getAxis(Xbox.AXIS.LEFT_Y);
        return (Math.abs(value ) > .2) ? value : 0;
    }

    public double getLiftStick()
    {
        return operator.getAxis(AXIS.RIGHT_Y);
    }

    public double getWinchPercent()
    {
        int direction = operator.getDpad();
        double speed = 0;

        if (direction == DPAD.UP.get() || direction == DPAD.UP_LEFT.get() || direction == DPAD.UP_RIGHT.get())
        {
            speed = Robot.config.WINCH_SPEED;
        }
        else if (direction == DPAD.DOWN.get() || direction == DPAD.DOWN_LEFT.get() || direction == DPAD.DOWN_RIGHT.get())
        {
            speed = -Robot.config.WINCH_SPEED;
        }

        return speed;
    }

    // ------------------------ Reuse ------------------------

    public static IO getInstance()
    {
        if (instance == null) { instance = new IO(new Joystick(0), new Joystick(1), new Xbox(2), new Panel(3)); }
        return instance;
    }

    public int getAutonomousSide()
    {
        return panel.getRotary();
    }

    public void setRumble(boolean rumble)
    {
        float state = (rumble) ? 1 : 0;
        operator.setRumble(Hand.kLeft, state);
        operator.setRumble(Hand.kRight, state);
    }
}