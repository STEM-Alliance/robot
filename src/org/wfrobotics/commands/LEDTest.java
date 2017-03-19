package org.wfrobotics.commands;

import java.util.ArrayList;

import org.wfrobotics.hardware.led.LEDs.Color;
import org.wfrobotics.hardware.led.LEDs.Effect;
import org.wfrobotics.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This command lets you easily cycle through different LED effects for testing purposes
 */
public class LEDTest extends Command
{
    private ArrayList<Effect> effects;
    private static int nextIndex; // Because this is static, the value will persist between different commands being kicked off. Another way to do this is passing an Effect to the Command's constructor.
    
    public LEDTest()
    {   
        Color[] fadeColors = {new Color(0, 255, 66), new Color(0, 255, 0)}; // Dark Greenish to Lime?
        
        effects = new ArrayList<Effect>();
        
        effects.add(new Effect(EFFECT_TYPE.BLINK, new Color(255, 0 , 0), 1));  // Blink Red
        effects.add(new Effect(EFFECT_TYPE.SOLID, new Color(0, 255, 66), .7)); // Solid Dark Greenish
        effects.add(new Effect(EFFECT_TYPE.FADE, fadeColors, .7));
        
        nextIndex = 0;
    }
    
    @Override
    protected void initialize()
    {
        Effect effect = effects.get(next());
        
        SmartDashboard.putString("LED Effect Mode", effect.type.toString());   
    
        Robot.leds.set(effect);
    }
    
    @Override
    protected boolean isFinished()
    {
        return true;
    }
    
    private int next()
    {
        int result = nextIndex++;
        
        SmartDashboard.putNumber("LED Effect Index", result);
        
        if (nextIndex > effects.size())
        {
            nextIndex = 0;
        }
        
        return result;
    }
}
