package org.wfrobotics.robot;

import org.wfrobotics.commands.LED;
import org.wfrobotics.controller.Xbox;
import org.wfrobotics.controller.XboxButton;

import edu.wpi.first.wpilibj.buttons.Button;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI 
{    
    static Xbox xboxDrive = new Xbox(0);    
    
    Button a = new XboxButton ( xboxDrive, Xbox.BUTTON.A);
    
    public OI()
    {        
      a.whenPressed(new LED());
    }
}

