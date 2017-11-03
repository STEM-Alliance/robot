package org.wfrobotics.prototype.config;

import org.wfrobotics.prototype.commands.ArmPivotBase;
import org.wfrobotics.prototype.commands.ArmPivotElbow;
import org.wfrobotics.prototype.commands.ArmPivotHand;
import org.wfrobotics.prototype.commands.HandSolenoid;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.controller.Xbox;

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

    public static Button RB = ButtonFactory.makeButton
            (controller, Xbox.BUTTON.RB, TRIGGER.WHILE_HELD, new ArmPivotHand(-.2));

    public static Button LB = ButtonFactory.makeButton
            (controller, Xbox.BUTTON.LB, TRIGGER.WHILE_HELD, new ArmPivotHand(.2));

    public static Button A = ButtonFactory.makeButton
            (controller, Xbox.BUTTON.A, TRIGGER.WHEN_PRESSED, new HandSolenoid(true));

    public static Button B = ButtonFactory.makeButton
            (controller, Xbox.BUTTON.B, TRIGGER.WHEN_PRESSED, new HandSolenoid(true));

    public static Button RightX = ButtonFactory.makeAxisButton
            (controller, Xbox.AXIS.RIGHT_X, .2, TRIGGER.WHEN_PRESSED, new ArmPivotBase());

    public static Button RightY = ButtonFactory.makeAxisButton
            (controller, Xbox.AXIS.RIGHT_Y, .2, TRIGGER.WHEN_PRESSED, new ArmPivotElbow());


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
}