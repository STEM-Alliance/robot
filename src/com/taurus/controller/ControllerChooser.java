package com.taurus.controller;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ControllerChooser {

    private SendableChooser controllerChooser;
    
    public static final int XBOX = 0;
    public static final int JOY = 1;
    
    public ControllerChooser()
    {

        controllerChooser = new SendableChooser();
        controllerChooser.addDefault("Xbox", XBOX);
        controllerChooser.addObject("Joysticks", JOY);
        SmartDashboard.putData("Controller", controllerChooser);
    }

    public ControllerSwerve GetController()
    {
       ControllerSwerve result;
       
       switch (((Integer)controllerChooser.getSelected()).intValue()))
       {
    	   case XBOX:
    		   result = new ControllerXbox();
    	   case JOY:
    		   result = new ControllerJoysticks();
    	   default:
    		   result = new ControllerXbox();
       }
    	
       return result;
    }
}
