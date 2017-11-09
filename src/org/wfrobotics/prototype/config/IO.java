package org.wfrobotics.prototype.config;

import org.wfrobotics.prototype.commands.ArmPivotBase;
import org.wfrobotics.prototype.commands.ArmPivotElbow;
import org.wfrobotics.prototype.commands.ArmPivotHand;
import org.wfrobotics.prototype.commands.HandSolenoid;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.controller.Xbox;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;

/** Maps Buttons to Commands **/
public class IO
{
    public static Xbox controller = new Xbox(0);

//
//    public static Button X = ButtonFactory.makeButton
//            (controller, Xbox.BUTTON.X, TRIGGER.WHILE_HELD, new ArmPivotBase(-.2));
//
//    public static Button Y = ButtonFactory.makeButton
//            (controller, Xbox.BUTTON.Y, TRIGGER.WHILE_HELD, new ArmPivotBase(.2));
//
//    public static Button A = ButtonFactory.makeButton
//            (controller, Xbox.BUTTON.A, TRIGGER.WHILE_HELD, new ArmPivotElbow(-.2));
//
//    public static Button B = ButtonFactory.makeButton
//            (controller, Xbox.BUTTON.B, TRIGGER.WHILE_HELD, new ArmPivotElbow(-.2));

    public static Button LT = ButtonFactory.makeAxisButton
            (controller, Xbox.AXIS.LEFT_TRIGGER, 0, TRIGGER.WHEN_PRESSED, new ArmPivotHand());

    public static Button RT = ButtonFactory.makeAxisButton
            (controller, Xbox.AXIS.RIGHT_TRIGGER, 0,  TRIGGER.WHEN_PRESSED, new ArmPivotHand());

    public static Button A = ButtonFactory.makeButton
            (controller, Xbox.BUTTON.A, TRIGGER.WHEN_PRESSED, new HandSolenoid(true));

    public static Button B = ButtonFactory.makeButton
            (controller, Xbox.BUTTON.B, TRIGGER.WHEN_PRESSED, new HandSolenoid(false));

//    public static Button RightX = ButtonFactory.makeAxisButton
//            (controller, Xbox.AXIS.RIGHT_X, .2, TRIGGER.WHEN_PRESSED, new ArmPivotBase());
//
//    public static Button RightY = ButtonFactory.makeAxisButton
//            (controller, Xbox.AXIS.RIGHT_Y, .2, TRIGGER.WHEN_PRESSED, new ArmPivotElbow());

    public double getLeftJoystick()
    {
        double value = controller.getAxis(Xbox.AXIS.LEFT_Y);

        if (Math.abs(value) < .2)
        {
            value = 0;
        }
        return value;
    }
    public double getRightJoystick()
    {
        double value = controller.getAxis(Xbox.AXIS.RIGHT_Y);

        if (Math.abs(value) < .2)
        {
            value = 0;
        }
        return value;
    }

    public double getXRotation()
    {
        double value = controller.getAxis(Xbox.AXIS.RIGHT_X);

        if (Math.abs(value) < .2)
        {
            value = 0;
        }
        return value;
    }
    
    public double getYRotation()
    {
        double value = controller.getAxis(Xbox.AXIS.RIGHT_Y);

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

//        if ((posValue) > 0)
//        {
//            value = posValue;
//        }
//        else
//        {
//            if(negValue > 0)
//            {
//            value = - negValue;
//            }
//        }
    }
    
}