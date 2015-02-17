package com.taurus.robotspecific2015;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.Timer;

public class LEDEffect {

    private Color[] ColorsCurrent;
    private ArrayList<Color[]> ColorsSettings;
    private double StartTime = -1;
    private double Duration = -1;
    private double Period = -1;
    private EFFECT Mode = EFFECT.SOLID;
    
    /**
     * Type of LED Effect that tells you what color to output over the effect duration
     * FADE: Move between two colors continuously
     * FLASH: Strobe one color
     * SPIN: Rotate colors
     * STEP: Move between discrete colors
     * SOLID: One color set to rule them all
     */
    public static enum EFFECT
    {
        FADE, FLASH, SPIN, STEP, SOLID
    }
    
    /**
     * Create a new LED Effect that will tell you what color to output over the effect duration
     */
    public LEDEffect(ArrayList<Color[]> settings, EFFECT mode, double duration, double period)
    {
        ColorsSettings = settings;
        ColorsCurrent = new Color[settings.get(0).length];
        Mode = mode;
        Duration = duration;        
        Period = period;
        
        // Initial color
        for (int index = 0; index < ColorsCurrent.length; index++)
        {
            ColorsCurrent[index] = Color.Black;
        }
    }
    
    public Color[] getColors()
    {
        double time = Timer.getFPGATimestamp() - StartTime;
        double percent = (time % Period) / Period;
        double stage = time / Period;  // How many periods in
        
        switch (Mode)
        {
            case FADE:
                for (int index = 0; index < ColorsCurrent.length; index++)
                {
                    ColorsCurrent[index] = Color.Fade(ColorsSettings.get(0)[index], ColorsSettings.get(1)[index], percent);
                }
                break;
            case FLASH:
                if (percent < .5)
                {
                    ColorsCurrent = ColorsSettings.get(0);
                }
                else
                {
                    ColorsCurrent = ColorsSettings.get(1);
                }
                break;
            case SPIN:            
                int spinOffset = (int) (stage * ColorsCurrent.length);  // Which offset into list of colors
                
                // Select circular index into the number of total color settings
                for (int index = 0; index < ColorsCurrent.length; index++)
                {
                    ColorsCurrent[index] = ColorsSettings.get(0)[Math.floorMod(index + spinOffset, ColorsCurrent.length)];
                }
                break;
            case STEP:  // TODO
                int stepOffset = (int) (stage * ColorsCurrent.length);  // Which offset into list of color settings
                
                ColorsCurrent = ColorsSettings.get(Math.floorMod(stepOffset, ColorsSettings.size())]);
                break;
            case SOLID:
            default:
                ColorsCurrent = ColorsSettings.get(0);
                break;
        }
        
        return ColorsCurrent;
    }
    
    public double getPeriod()
    {
        return Period;
    }
    
    public void start()
    {
        StartTime = Timer.getFPGATimestamp();
    }
    
    public boolean isDone()
    {
        boolean result = false;
        
        if (StartTime > 0)
        {
            result = Timer.getFPGATimestamp() - StartTime < Duration;
        }

        return result;
    }
}
