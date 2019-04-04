package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.AutoFactory;
import org.wfrobotics.reuse.config.ButtonFactory;
import org.wfrobotics.reuse.config.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.config.HerdJoystick;
import org.wfrobotics.reuse.config.EnhancedIO;
import org.wfrobotics.reuse.config.Xbox;
import org.wfrobotics.robot.commands.climb.PullForward;
import org.wfrobotics.robot.commands.elevator.ElevatorGoHome;
import org.wfrobotics.robot.commands.experimental.SmartHatch;
import org.wfrobotics.robot.commands.intake.CargoIn;
import org.wfrobotics.robot.commands.intake.CargoOut;
import org.wfrobotics.robot.commands.intake.IntakeHatchManual;
import org.wfrobotics.robot.commands.intake.ScoreHatch;
import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.commands.system.SystemToHigh;
import org.wfrobotics.robot.commands.system.SystemToLow;
import org.wfrobotics.robot.commands.system.SystemToMiddle;
import org.wfrobotics.robot.subsystems.Elevator;
import org.wfrobotics.robot.commands.system.SystemPickup;
import org.wfrobotics.robot.commands.system.SystemToCargoBay;
import org.wfrobotics.robot.commands.system.SystemToCargoBoxPickup;
import org.wfrobotics.robot.commands.elevator.ElevatorShift;
import org.wfrobotics.robot.commands.elevator.PIDClimb;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;

/** Maps controllers to Commands **/
public final class IO implements EnhancedIO
{
    private static IO instance = null;
    private final HerdJoystick driverThrottle;  // TODO Refactor - Make Button from JoyStick instead?
    private final Joystick driverTurn;
    private final Xbox operator;
    private final Xbox rocketPlate;

    /** Create and configure controls for Drive Team */
    private IO()
    {
        driverThrottle = new HerdJoystick(0);
        driverTurn = new Joystick(1);

        operator = new Xbox(3);
        rocketPlate = new Xbox(2);
    }

    /** Configure each Button to run a Command */
    public void assignButtons()
    {
        // ---------------------- Autonomous ----------------------
        ButtonFactory.makeButton(driverThrottle, 7, TRIGGER.WHEN_PRESSED, AutoFactory.getInstance().makeCommand(Auto.modes));
        ButtonFactory.makeButton(driverThrottle, 8, TRIGGER.WHEN_PRESSED, AutoFactory.getInstance().makeCommand(Auto.delays));
        ButtonFactory.makeButton(driverThrottle, 9, TRIGGER.WHEN_PRESSED, AutoFactory.getInstance().makeCommand(Auto.positions));

        //----------------------- Climber -------------------------

        //----------------------- Driver --------------------------

        //----------------------- Elevator ------------------------

        //----------------------- Intake --------------------------
        // ButtonFactory.makeButton(operator, Xbox.BUTTON.X, TRIGGER.WHEN_PRESSED,);
        // ButtonFactory.makeButton(operator, Xbox.BUTTON.Y, TRIGGER.WHEN_PRESSED, );

        ButtonFactory.makeButton(operator, Xbox.DPAD.UP, TRIGGER.WHEN_PRESSED, new CargoIn() );
        ButtonFactory.makeButton(operator, Xbox.DPAD.DOWN, TRIGGER.WHEN_PRESSED, new CargoOut(.75));

        ButtonFactory.makeButton(operator, Xbox.BUTTON.LB, TRIGGER.WHILE_HELD, new IntakeHatchManual(true));
        ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new ElevatorShift(true));//into climb mode
        ButtonFactory.makeButton(operator, Xbox.BUTTON.START, TRIGGER.WHEN_PRESSED, new ElevatorShift(false));

        ButtonFactory.makeButton(operator, Xbox.DPAD.RIGHT, TRIGGER.WHEN_PRESSED, new PIDClimb(true)); // into climb mode
        ButtonFactory.makeButton(operator, Xbox.DPAD.LEFT, TRIGGER.TOGGLE_WHEN_PRESSED, new PullForward());
        


        //----------------------- System --------------------------
        ButtonFactory.makeButton(rocketPlate, Xbox.BUTTON.A, TRIGGER.WHEN_PRESSED, new SystemToHigh()); // top button on rocket
        ButtonFactory.makeButton(rocketPlate, Xbox.BUTTON.B, TRIGGER.WHEN_PRESSED, new SystemToMiddle()); // middle button on rocket
        ButtonFactory.makeButton(rocketPlate, Xbox.BUTTON.X, TRIGGER.WHEN_PRESSED, new SystemToLow()); // bottem button on rocket
        ButtonFactory.makeButton(rocketPlate, Xbox.BUTTON.LB, TRIGGER.WHEN_PRESSED, new SystemPickup()); // bottem button on breakout board

        ButtonFactory.makeButton(rocketPlate, Xbox.BUTTON.Y, TRIGGER.WHEN_PRESSED, new SystemToCargoBoxPickup()); // middle button on breakout board
        ButtonFactory.makeButton(rocketPlate, Xbox.BUTTON.RB, TRIGGER.WHEN_PRESSED, new  SystemToCargoBay()); // top button on breakout board
        // mystory button added!!!!



        //----------------------- Testing -------------------------

        // ButtonFactory.makeButton(operator, Xbox.BUTTON.START, TRIGGER.WHEN_PRESSED, new LinkToHeight(45.0));
        // ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new LinkToHeight(10.0));

    }

    // ------------------- Robot-specific --------------------

    public double getCargoStick()
    {
    	return -operator.getX(Hand.kLeft);
    }

    public double getElevatorStick()
    {
        return operator.getY(Hand.kRight);
    }
    public double getClimbArmsStick()
    {
        return operator.getY(Hand.kLeft);
    }

    public double getIntakeStick()
    {
        return operator.getX(Hand.kRight);
    }

    public double getLinkDown()
    {
        return operator.getTrigger(Hand.kRight);
    }

    public double getLinkUp()
    {
        return operator.getTrigger(Hand.kLeft);
    }

    public boolean isElevatorOverrideRequested()
    {
        return  Math.abs(getElevatorStick()) > 0.15;
    }

    public boolean isLinkOverrideRequested()
    {
        return  (Math.abs(getLinkDown()) > 0.15) || (Math.abs(getLinkUp()) > 0.15);
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
        return driverThrottle.getButtonPressed(1);
    }

    public boolean isDriveOverrideRequested()
    {
        return Math.abs(getThrottle()) > 0.15 || Math.abs(getTurn()) > 0.15;
    }

    public void setRumble(boolean rumble)
    {
        double state = (rumble) ? 1.0 : 0.0;
        operator.setRumble(Hand.kLeft, state);
        operator.setRumble(Hand.kRight, state);
    }
}