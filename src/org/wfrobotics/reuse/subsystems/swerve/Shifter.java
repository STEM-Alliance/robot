package org.wfrobotics.reuse.subsystems.swerve;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;

/** Swerve Drive Shifter that shifts each Swerve Wheel */
public class Shifter
{
    private final int TOP;
    private final int BOTTOM;
    
    private final Servo shifter;

    private boolean gearLastState;
    private boolean gearLastRequested;
    private double timeLastRequested;
    
    public Shifter(int servoPin, int angleMidway, int angleRange, boolean invert)
    {
        int halfRange = (invert) ? -angleRange / 2 : angleRange / 2;
        
        shifter = new Servo(servoPin);
        TOP = angleMidway + halfRange;
        BOTTOM = angleMidway - halfRange;
        gearLastState = false;
        gearLastRequested = false;
        timeLastRequested = Timer.getFPGATimestamp() - Constants.SHIFTER_SHIFT_TIME;  // Start fully in this gear
    }
    
    /**
     * Shift. Safe to repeatedly call
     * @param useHighGear True: High gear, False: Low gear 
     */
    public void setGear(boolean useHighGear)
    {
        shifter.setAngle((useHighGear) ? TOP : BOTTOM);
        
        // Only start a transition if we are changing
        // Important: This allows the caller to set the shifter repeatedly and not be indefinitely transitioning in the getter()
        if(gearLastRequested != useHighGear)
        {
            gearLastRequested = useHighGear;
            timeLastRequested = Timer.getFPGATimestamp();
        }
    }
    
    /**
     * Get the current gear the shifter is in
     * Note: If we have not fully shifted yet, this returns the last state
     * @return True: High gear, False: Low gear
     */
    public boolean isHighGear()
    {
        if (Timer.getFPGATimestamp() - timeLastRequested > Constants.SHIFTER_SHIFT_TIME)
        {
            gearLastState = gearLastRequested;
        }
        
        return gearLastState;
    }
}