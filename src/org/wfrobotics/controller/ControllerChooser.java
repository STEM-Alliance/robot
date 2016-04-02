package org.wfrobotics.controller;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ControllerChooser {

    private SendableChooser controllerChooser;

    public static final int XBOX = 0;
    public static final int JOY = 1;

    public ControllerChooser()
    {

        controllerChooser = new SendableChooser();
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
            default:
                result = new SwerveXbox();
                break;
        }

        return result;
    }
}
