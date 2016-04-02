package org.wfrobotics.led;

import java.util.ArrayList;

import org.wfrobotics.Utilities;

import edu.wpi.first.wpilibj.Timer;

public class Effect {

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
     * @param settings colors (related to mode)
     * @param mode which effect to general over time
     * @param duration time until the effect is done (seconds)
     * @param period time to repeat the effect, if period < duration (seconds)
     */
    public Effect(ArrayList<Color[]> settings, EFFECT mode, double duration, double period)
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
    
    /**
     * What are the current colors of this effect, which change with time
     * @return colors at this time
     */
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
                    ColorsCurrent[index] = ColorsSettings.get(0)[Utilities.floorMod(index + spinOffset, ColorsCurrent.length)];
                }
                break;
            case STEP:
                int stepOffset = (int) (stage * ColorsCurrent.length);  // Which offset into list of color settings
                
                ColorsCurrent = ColorsSettings.get(Utilities.floorMod(stepOffset, ColorsSettings.size()));
                break;
            case SOLID:
            default:
                ColorsCurrent = ColorsSettings.get(0);
                break;
        }
        
        return ColorsCurrent;
    }
    
    /**
     * How often is this effect changing?
     * @return period
     */
    public double getPeriod()
    {
        return Period;
    }

    /**
     * Start the effect, allowing it to alter returned color(s) over time
     */
    public void start()
    {
        StartTime = Timer.getFPGATimestamp();
    }
    
    /**
     * Get if the effect duration has expired
     * 
     * @return Done?
     */
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
