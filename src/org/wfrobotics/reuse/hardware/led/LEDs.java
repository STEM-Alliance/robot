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
    public static final Color SALMON = new Color (250, 128, 114);
    public static final Color ORANGE = new Color(255, 69, 0);
    public static final Color YELLOW = new Color(255, 255, 0);
    public static final Color GOLD = new Color (255, 215, 0);
    public static final Color GREEN = new Color(0, 128, 0);
    public static final Color LIME = new Color(0, 255, 0);
    public static final Color LIME_GREEN = new Color(50, 205, 50);
    public static final Color LIGHT_GREEN = new Color(144, 238, 144);
    public static final Color DARK_GREEN = new Color(0, 100, 0);
    public static final Color CHARTREUSE = new Color(127, 255, 0);
    public static final Color CYAN = new Color(0, 255, 255);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color MAGENTA = new Color(255, 0, 255);
    public static final Color PURPLE = new Color(128, 0, 128);
    public static final Color INDIGO = new Color(75, 0, 130);
    public static final Color PLUM = new Color(142, 69, 133);
    public static final Color HOT_PINK = new Color(255, 105, 180);
    public static final Color PINK = new Color(255, 192, 203);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color SILVER = new Color(192, 192, 192);
    
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
