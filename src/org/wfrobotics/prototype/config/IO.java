package org.wfrobotics.prototype.config;

import org.wfrobotics.prototype.commands.DriveArcade;
import org.wfrobotics.prototype.commands.Elevate;
import org.wfrobotics.prototype.commands.IntakePull;
import org.wfrobotics.prototype.commands.IntakePush;
import org.wfrobotics.prototype.commands.Shifting;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.controller.Xbox;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;

/** Maps Buttons to Commands **/
public class IO
{
    public static Xbox controller = new Xbox(0);
    public static Xbox man= new Xbox(1);

    public static Button manB = ButtonFactory.makeButton
            (man, Xbox.BUTTON.B, TRIGGER.WHILE_HELD, new IntakePush(0.7));

    public static Button manA = ButtonFactory.makeButton
            (man, Xbox.BUTTON.A, TRIGGER.WHILE_HELD, new IntakePull(.3));

    public static Button manRB = ButtonFactory.makeButton
            (man, Xbox.BUTTON.RB,TRIGGER.WHILE_HELD, new Elevate(-1));

    public static Button manLB = ButtonFactory.makeButton
            (man, Xbox.BUTTON.LB,  TRIGGER.WHILE_HELD, new Elevate(1));


    public static Button B = ButtonFactory.makeButton
            (controller, Xbox.BUTTON.B, TRIGGER.WHILE_HELD, new IntakePush());

    public static Button A = ButtonFactory.makeButton
            (controller, Xbox.BUTTON.A, TRIGGER.WHILE_HELD, new IntakePull());

    public static Button Start = ButtonFactory.makeButton
            (controller, Xbox.BUTTON.START, TRIGGER.TOGGLE_WHEN_PRESSED, new DriveArcade());

    public static Button RB = ButtonFactory.makeButton
            (controller, Xbox.BUTTON.RB,TRIGGER.WHILE_HELD, new Elevate(-1));

    public static Button LB = ButtonFactory.makeButton
            (controller, Xbox.BUTTON.LB,  TRIGGER.WHILE_HELD, new Elevate(1));


    public static Button Back= ButtonFactory.makeButton
            (controller, Xbox.BUTTON.BACK,  TRIGGER.TOGGLE_WHEN_PRESSED, new Shifting(true));

/*
    public static Button RightY = ButtonFactory.makeAxisButton
            (controller, Xbox.AXIS.RIGHT_Y, .2, TRIGGER.WHEN_PRESSED, new ArmPivotElbow());

    public static Button RightX= ButtonFactory.makeAxisButton
            (controller, Xbox.AXIS.RIGHT_X, .2, TRIGGER.WHEN_PRESSED, new ArmPivotBase());
*/
    public double getLeftX()
    {
        double value = controller.getAxis(Xbox.AXIS.LEFT_X);

        if (Math.abs(value) < .2)
        {
            value = 0;
        }
        return value;
    }

    public double getLeftY()
    {
        double value = controller.getAxis(Xbox.AXIS.LEFT_Y);

        if (Math.abs(value) < .2)
        {
            value = 0;
        }
        return value;
    }
    public double getRightY()
    {
        double value = controller.getAxis(Xbox.AXIS.RIGHT_Y);

        if (Math.abs(value) < .2)
        {
            value = 0;
        }
        return value;
    }

    public double getRightX()
    {
        double value = controller.getAxis(Xbox.AXIS.RIGHT_X);

        if (Math.abs(value) < .2)
        {
            value = 0;
        }
        return value;
    }

    public double getTriggerRotation()
    {
          double value = 0;

          double posValue = controller.getTrigger(Hand.kRight);
          double negValue = controller.getTrigger(Hand.kLeft);

          value = posValue - negValue;

          return value;
    }

}