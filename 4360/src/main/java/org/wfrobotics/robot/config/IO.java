package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.ButtonFactory;
import org.wfrobotics.reuse.config.EnhancedIO;
import org.wfrobotics.reuse.config.HerdJoystick;
import org.wfrobotics.reuse.config.SpudJoystick;
import org.wfrobotics.reuse.config.Xbox;
import org.wfrobotics.robot.commands.IntakeMotorControl;
import org.wfrobotics.robot.commands.IntakeOpenClose;
import org.wfrobotics.robot.commands.LiftManual;
import org.wfrobotics.robot.commands.ParallelLinkControl;
import org.wfrobotics.robot.commands.Testbutton;
import org.wfrobotics.reuse.config.ButtonFactory.TRIGGER;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;

/** Maps controllers to Commands **/
public final class IO implements EnhancedIO
{
    private static IO instance = null;
    private final HerdJoystick driverThrottle;
    private final HerdJoystick driverTurn;
    private final Xbox operator;
    private final Xbox drive;
    public final double inSpeed = 1;
    public final double outSpeed = -1;

    private IO()
    {
    //  driverThrottle = new SpudJoystick(0);
    //  driverTurn = new SpudJoystick(1);
        driverThrottle = new HerdJoystick(0);
        driverTurn = new HerdJoystick(1);
        operator = new Xbox(2);
    	drive = new Xbox(2);
    }

    public void assignButtons()
    {
        // ------------------------- Drive ------------------------

        // ------------------------ Intake ------------------------
    	
    	//------Xbox--------
    	 //	robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.AXIS.LEFT_TRIGGER, .1, TRIGGER.WHILE_HELD, new setIntakeMotorSpeed(inSpeed)));
    	 //	robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.AXIS.RIGHT_TRIGGER, .1, TRIGGER.WHILE_HELD, new setIntakeMotorSpeed(outSpeed)));
    	 //	ButtonFactory.makeButton(operator, Xbox.AXIS.LEFT_Y, );
    	 //	ButtonFactory.makeButton(operator, Xbox.BUTTON.A, TRIGGER.WHEN_PRESSED, new IntakeOpenClose(true));
         // ButtonFactory.makeButton(operator, Xbox.BUTTON.B, TRIGGER.WHEN_PRESSED, new IntakeOpenClose(false));

    	
    		// ButtonFactory.makeButton(operator, Xbox.BUTTON.RB, TRIGGER.WHILE_HELD, new IntakeMotorControl(0.25));
    		// ButtonFactory.makeButton(operator, Xbox.BUTTON.LB, TRIGGER.WHILE_HELD, new IntakeMotorControl(-0.25));
    		// ButtonFactory.makeButton(operator, Xbox.BUTTON.A, TRIGGER.WHILE_HELD, new ParallelLinkControl(1.0));
    		// ButtonFactory.makeButton(operator, Xbox.BUTTON.B, TRIGGER.WHILE_HELD, new ParallelLinkControl(-1.0));
    		// ButtonFactory.makeButton(operator, Xbox.BUTTON.X, TRIGGER.WHILE_HELD, new LiftManual(0.5));
    		// ButtonFactory.makeButton(operator, Xbox.BUTTON.Y, TRIGGER.WHILE_HELD, new LiftManual(-0.5));
   		
    	//------Joysticks--------
         // ButtonFactory.makeButton(driverTurn, SpudJoystick.BUTTON.THUMB_TOP_RIGHT, TRIGGER.WHILE_HELD, new Testbutton());
            // ButtonFactory.makeButton(driverTurn, BUTTON.BUTTON1.value, TRIGGER.WHILE_HELD, new Testbutton());
            ButtonFactory.makeButton(driverThrottle, 1, TRIGGER.WHILE_HELD, new IntakeMotorControl(0.25));
    		ButtonFactory.makeButton(driverTurn, 1, TRIGGER.WHILE_HELD, new IntakeMotorControl(-0.25));
    		ButtonFactory.makeButton(driverThrottle, 3, TRIGGER.WHILE_HELD, new ParallelLinkControl(1.0));
    		ButtonFactory.makeButton(driverTurn, 3, TRIGGER.WHILE_HELD, new ParallelLinkControl(-1.0));
    		// ButtonFactory.makeButton(driverThrottle, 3, TRIGGER.WHILE_HELD, new LiftManual(0.5));
    		// ButtonFactory.makeButton(driverTurn, 3, TRIGGER.WHILE_HELD, new LiftManual(-0.5));
 
        // -------------------- Super Structure -------------------
         //test ButtonFactory.makeButton(operator, Xbox.DPAD.RIGHT, TRIGGER.WHILE_HELD, new SignalHumanPlayer());

        // ------------------------ Debug -------------------------
         // robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.START, TRIGGER.WHEN_PRESSED, new DrivePathPosition("Path")));
         // robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new TestAuto()));
         // robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new DrivePathVelocity("Path")));
         // robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new DriveDistance(12.0 * 5)));
         // robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.START, TRIGGER.WHEN_PRESSED, new TurnToHeading(0.0 , 2.0)));
         // robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new TurnToHeading(90.0 , 2.0)));
         // robotSpecific.add(ButtonFactory.makeButton(operator, Xbox.BUTTON.START, TRIGGER.WHEN_PRESSED, new PickLocation()));
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
        return driverThrottle.getY();
    //  return 0.0;
    }

    public double getTurn()
    {
        return Math.signum(-driverTurn.getX())*Math.pow(driverTurn.getX(), 2);
    //	return  0.0;
    }

    public boolean getDriveQuickTurn()
    {
        return -driverTurn.getY() < 0.05;
    }
    
    public boolean isDriveOverrideRequested()
    {
    	return Math.abs(this.getThrottle()) > 0.15 || Math.abs(this.getTurn()) > 0.15;
    }

    public void setRumble(boolean rumble)
    {
    //  float state = (rumble) ? 1 : 0;
    //  operator.setRumble(Hand.kLeft, state);
    //  operator.setRumble(Hand.kRight, state);
    }
    
    public double getRight() 
    {
    	return -0.65*(drive.getY(Hand.kRight));
    }
    
    public double getLeft() 
    {
    	return -0.65*(drive.getY(Hand.kLeft));
    }

    public static enum BUTTON
    {
        THUMB_TOP_RIGHT(1),
        THUMB_BOTTOM_RIGHT(2),
        THUMB_TOP_LEFT(3),
        THUMB_BOTTOM_LEFT(4),
        TRIGGER(5),
        THUMB_SIDE(6),
        BASE_TOP_RIGHT(7),
        BASE_MIDDLE_RIGHT(8),
        BASE_BOTTOM_RIGHT(9),
        BASE_TOP_LEFT(10),
        BASE_MIDDLE_LEFT(11),
        BASE_BOTTOM_LEFT(12),

        BUTTON1(1),
        BUTTON2(2),
        BUTTON3(3),
        BUTTON4(4),
        BUTTON5(5),
        BUTTON6(6),
        BUTTON7(7),
        BUTTON8(8),
        BUTTON9(9),
        BUTTON10(10),
        BUTTON11(11),
        BUTTON12(12);

        private final int value;

        private BUTTON(int value) { this.value = value; }
        public int get() { return value; }
    }
}

