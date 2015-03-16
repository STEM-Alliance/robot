package com.taurus.controller;

import com.taurus.robotspecific2015.ControllerXboxPanel;

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
        controllerChooser.addDefault("Xbox & Panel", XBOXPANEL);
        controllerChooser.addObject("Xbox", XBOX);
        controllerChooser.addObject("Joysticks", JOY);
        SmartDashboard.putData("Controller", controllerChooser);
    }

    public SwerveController GetController()
    {
        SwerveController result;

        switch (((Integer) controllerChooser.getSelected()).intValue())
        {
            case XBOX:
                result = new SwerveXbox();
                break;
            case JOY:
                result = new SwerveJoysticks();
                break;
            case XBOXPANEL:
                result = new ControllerXboxPanel();
                break;
            default:
                result = new SwerveXbox();
                break;
        }

        return result;
    }
}
