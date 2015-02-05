package com.taurus.controller;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ControllerChooser {

    private SendableChooser controllerChooser;

    public static final int XBOX = 0;
    public static final int JOY = 1;
    public static final int XBOXPANEL = 2;

    public ControllerChooser()
    {

        controllerChooser = new SendableChooser();
        controllerChooser.addDefault("Xbox", XBOX);
        controllerChooser.addObject("Joysticks", JOY);
        controllerChooser.addObject("Xbox & Panel", XBOXPANEL);
        SmartDashboard.putData("Controller", controllerChooser);
    }

    public Controller GetController()
    {
        Controller result;

        switch (((Integer) controllerChooser.getSelected()).intValue())
        {
            case XBOX:
                result = new ControllerXbox();
                break;
            case JOY:
                result = new ControllerJoysticks();
                break;
            case XBOXPANEL:
                result = new ControllerXboxPanel();
                break;
            default:
                result = new ControllerXbox();
                break;
        }

        return result;
    }
}
