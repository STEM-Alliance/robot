package com.taurus.robotspecific2015;

import edu.wpi.first.wpilibj.Timer;

public class LEDEffect {

    private Color[] ColorsCurrent;
    private Color[] ColorsStart;
    private Color[] ColorsEnd;
    private double StartTime = -1;
    private double Duration = 1;
    private double Period = 1;
    private EFFECT Mode = EFFECT.SOLID;
    
    public static enum EFFECT
    {
        FADE, FLASH, SPIN, SOLID
    }
    
    public LEDEffect(Color[] start, Color[] end, double duration)
    {
        ColorsStart = start;
        ColorsEnd = end;
        ColorsCurrent = new Color[start.length];
        Duration = duration;
        
        // Initial color
        for (int index = 0; index < ColorsCurrent.length; index++)
        {
            ColorsCurrent[index] = Color.White;
        }
    }
    
    public void setMode(EFFECT mode, double period)
    {
        Mode = mode;
        Period = period;
    }
    
    public Color[] getColors()
    {
        double time = Timer.getFPGATimestamp() - StartTime;
        double percent = (time % Period) / Period;
        
        switch (Mode)
        {
            case FADE:
                for (int index = 0; index < ColorsStart.length; index++)
                {
                    ColorsCurrent[index] = Color.Fade(ColorsStart[index], ColorsEnd[index], percent);
                }
                break;
            case FLASH:
                for (int index = 0; index < ColorsStart.length; index++)
                {
                    if (percent < .5)
                    {
                        ColorsCurrent[index] = ColorsStart[index];
                    }
                    else
                    {
                        ColorsCurrent[index] = ColorsEnd[index];
                    }
                }
                break;
            case SPIN:
                double stage = time / Period;  // How many periods in
                int offset = (int) (stage * ColorsStart.length);  // Which offset into list of colors
                
                for (int index = 0; index < ColorsStart.length; index++)
                {
                    ColorsCurrent[index] = ColorsStart[Math.floorMod(index + offset, ColorsStart.length)];
                }
                break;
            case SOLID:
            default:
                for (int index = 0; index < ColorsStart.length; index++)
                {
                    ColorsCurrent[index] = ColorsStart[index];
                }
                break;
        }
        
        return ColorsCurrent;
    }
    
    public void start()
    {
        StartTime = Timer.getFPGATimestamp();
    }
    
    public boolean done()
    {
        return Timer.getFPGATimestamp() - StartTime < Duration;
    }
}
