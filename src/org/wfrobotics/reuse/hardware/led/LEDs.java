package org.wfrobotics.reuse.hardware.led;

public class LEDs 
{    
    public static class Color
    {
        public final int r, g, b;
        
        public Color(int r, int g, int b)
        {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }
    
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color MAROON = new Color(128, 0, 0);
    public static final Color ORANGE = new Color(255, 69, 0);
    public static final Color YELLOW = new Color(255, 255, 0);
    public static final Color GOLD = new Color (255, 215, 0);
    public static final Color LIME = new Color(0, 255, 0);
    public static final Color DARK_GREEN = new Color(0, 100, 0);
    public static final Color CYAN = new Color(0, 255, 255);
    public static final Color BLUE = new Color(0, 0, 255);
    
    public static Color[] COLORS_RED_ALLIANCE = {RED , MAROON};
    public static Color[] COLORS_BLUE_ALLIANCE = {BLUE, CYAN};
    public static Color[] COLORS_THE_HERD = {DARK_GREEN, LIME, YELLOW};
    
    public static class Effect
    {
        public static enum EFFECT_TYPE {OFF, SOLID, BLINK, CYCLE, FADE}
        
        public final EFFECT_TYPE type;
        public final Color[] colors;
        public final double time;
        
        public Effect(EFFECT_TYPE type, Color color, double time)
        {
            Color[] colors = {color};
            
            this.type = type;
            this.colors = colors;
            this.time = time;
        }
        
        public Effect(EFFECT_TYPE type, Color[] colors, double time)
        {
            this.type = type;
            this.colors = colors;
            this.time = time;
        }
    }
    
    /**
     * Interface for hardware that implements The Herd's reuse LEDs effects
     */
    public interface LEDController
    {
        /**
         * Call this to set the LEDs. This persists until the next caller calls it.
         * @param e How should the LEDs behave?
         */
        public void set(Effect e);
        /**
         * Call this to set the LEDs. This persists until the next caller calls it.
         * @param type How should the LEDs change over time?
         * @param colors How many colors are involved in this effect?
         * @param time How long should each color involved be displayed? (Units: Seconds)
         */
        public void set(Effect.EFFECT_TYPE type, Color[] colors, double time);
        /**
         * Don't want LEDs doing stuff? Are they disconnected? Use this to disable the code rather than commenting it out.
         * @param enable Use this to prevent calls to the hardware
         */
        public void enable(boolean enable);
    }
}
