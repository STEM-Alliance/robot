package org.wfrobotics.robot.driveoi;

import java.util.ArrayList;

import org.wfrobotics.reuse.commands.differential.DriveArcade;
import org.wfrobotics.reuse.commands.driveconfig.ShiftToggle;
import org.wfrobotics.reuse.controller.ButtonFactory;
import org.wfrobotics.reuse.controller.ButtonFactory.TRIGGER;
import org.wfrobotics.reuse.controller.Xbox;
import org.wfrobotics.reuse.utilities.HerdVector;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;

public class Tank
{
    public interface TankIO
    {
        public double getL();
        public double getR();
        public double getThrottleSpeedAdjust();


        public double getRocketRotation();
        public double getRocketSpeed();
        public HerdVector getVector();
    }

    public static class TankXbox implements TankIO
    {
        private final Xbox controller;
        private final ArrayList<Button> buttons = new ArrayList<Button>();  // Keep buttons instantiated

        public TankXbox(Xbox controller)
        {
            this.controller = controller;

            buttons.add(ButtonFactory.makeButton(controller, Xbox.BUTTON.START, TRIGGER.TOGGLE_WHEN_PRESSED, new DriveArcade()));
            buttons.add(ButtonFactory.makeButton(controller, Xbox.BUTTON.BACK, TRIGGER.WHEN_PRESSED, new ShiftToggle()));
        }

        public HerdVector getVector()
        {
            return controller.getVector(Hand.kLeft);
        }

        public double getL()
        {
            return controller.getY(Hand.kLeft);
        }

        public double getR()
        {
            return controller.getY(Hand.kRight);
        }

        public double getRocketRotation()
        {
            return controller.getX(Hand.kLeft);
        }

        public double getRocketSpeed()
        {
            double value = 0;

            double posValue = controller.getTrigger(Hand.kRight);
            double negValue = controller.getTrigger(Hand.kLeft);

            value = posValue - negValue;

            return value;
        }

        public double getThrottleSpeedAdjust()
        {
            return 0.5 + .5 * controller.getTrigger(Hand.kLeft);
        }
    }
}