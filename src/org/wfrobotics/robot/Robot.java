
package org.wfrobotics.robot;

import org.wfrobotics.hardware.led.LEDs.Color;
import org.wfrobotics.hardware.led.LEDs.Effect;
import org.wfrobotics.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.hardware.led.LEDs.LEDController;
import org.wfrobotics.hardware.led.LEDs;
import org.wfrobotics.hardware.led.MindsensorCANLight;

import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.SampleRobot;

public class Robot extends SampleRobot 
{      
    public static OI oi;
    public static LEDController leds;
    
    /**
     * This function is run when the robot is first started up and should be used for any initialization code
     */
    public void robotInit() 
    {
        leds = new MindsensorCANLight(RobotMap.CAN_LIGHT[0]);
        oi = new OI();
    }

    public void operatorControl()
    {
        // Run a command to prove we can control the LEDs outside Commands
        leds.set(new Effect(EFFECT_TYPE.BLINK, LEDs.SALMON, 1));
        
        while (isOperatorControl() && isEnabled())
        {
            Scheduler.getInstance().run();
        }
    }
    
    public void autonomous()
    {
        // Run a command to prove we can control the LEDs outside Commands
        leds.set(new Effect(EFFECT_TYPE.BLINK, LEDs.LIME, 1));
    }
    
    public void disabled()
    {
        Color[] colors = {LEDs.GREEN, LEDs.YELLOW};

        // Run a command to prove we can control the LEDs outside Commands
        leds.set(new Effect(EFFECT_TYPE.FADE, colors, 2));
    }
    
    public void test()
    {
        leds.set(new Effect(EFFECT_TYPE.SOLID, new Color(0, 0, 0), 1));
    }
}
