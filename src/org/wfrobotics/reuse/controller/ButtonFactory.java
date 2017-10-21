package org.wfrobotics.reuse.controller;

import org.wfrobotics.reuse.controller.Xbox.AXIS;
import org.wfrobotics.reuse.controller.Xbox.DPAD;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.command.Command;

public abstract class ButtonFactory
{
    public enum TRIGGER
    {
        CANCEL_WHEN_PRESSED,
        TOGGLE_WHEN_PRESSED,
        WHEN_PRESSED,
        WHEN_RELEASED,
        WHILE_HELD;
    }

    public static Button makeButton(Xbox provider, Xbox.BUTTON button, TRIGGER when, Command action)
    {
        return setTrigger(new XboxButton(provider, button), when, action);
    }

    public static Button makeButton(Xbox provider, Hand side, double thresholdOn, TRIGGER when, Command action)
    {
        return setTrigger(new XboxTriggerButton(provider, side, thresholdOn), when, action);
    }

    public static Button makeButton(Xbox provider, DPAD button, TRIGGER when, Command action)
    {
        return setTrigger(new XboxDpadButton(provider, button), when, action);
    }

    public static Button makeButton(Panel provider, Panel.BUTTON button, TRIGGER when, Command action)
    {
        return setTrigger(new PanelButton(provider, button), when, action);
    }

    private static Button setTrigger(Button b, TRIGGER when, Command action)
    {
        switch (when)
        {
            case CANCEL_WHEN_PRESSED:
                b.cancelWhenPressed(action);
                break;
            case TOGGLE_WHEN_PRESSED:
                b.toggleWhenPressed(action);
                break;
            case WHEN_PRESSED:
                b.whenPressed(action);
                break;
            case WHEN_RELEASED:
                b.whenReleased(action);
                break;
            case WHILE_HELD:
            default:
                b.whileHeld(action);
                break;
        }
        return b;
    }

    private static class XboxButton extends Button
    {
        Xbox hardware;
        Xbox.BUTTON button;

        public XboxButton(Xbox hardware, Xbox.BUTTON button)
        {
            this.hardware = hardware;
            this.button = button;
        }

        public boolean get()
        {
            return hardware.getButtonPressed(button);
        }
    }

    private static class XboxDpadButton extends Button
    {
        Xbox hardware;
        DPAD direction;

        public XboxDpadButton(Xbox hardware, DPAD direction)
        {
            this.hardware = hardware;
            this.direction = direction;
        }

        public boolean get()
        {
            return hardware.getButtonPressed(direction);
        }
    }

    private static class XboxTriggerButton extends Button
    {
        Xbox hardware;
        AXIS a;
        double limit;

        public XboxTriggerButton(Xbox hardware, Hand hand, double thresholdOn)
        {
            this.hardware = hardware;
            a = (hand == Hand.kLeft) ? AXIS.LEFT_TRIGGER : AXIS.RIGHT_TRIGGER;
            limit = thresholdOn;
        }

        public boolean get()
        {
            return hardware.getAxis(a) > limit;
        }
    }

    private static class PanelButton extends Button
    {
        Panel hardware;
        Panel.BUTTON button;

        public PanelButton(Panel hardware, Panel.BUTTON button)
        {
            this.hardware = hardware;
            this.button = button;
        }

        public boolean get()
        {
            return hardware.getButton(button);
        }
    }

}
