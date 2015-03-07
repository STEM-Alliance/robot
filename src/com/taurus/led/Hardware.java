package com.taurus.led;

/**
 * Device controls hardware required to display colors
 */
public abstract class Hardware {
    
    /**
     * Setup the hardware
     */
    public abstract void init();
    
    /**
     * Updates LEDs with these colors
     * @param colors Colors to display
     */
    public abstract void update(Color[] colors);
}
