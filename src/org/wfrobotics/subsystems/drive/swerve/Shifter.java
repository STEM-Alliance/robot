package org.wfrobotics.subsystems.drive.swerve;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;

/** Swerve Drive Shifter that shifts each Swerve Wheel */
public class Shifter
{
    private final Servo shifter;
    private final int angleH;
    private final int angleL;
    private boolean gearLastState;
    private boolean gearLastRequested;
    private double timeLastRequested;
    
    public Shifter(int servoPin, int servoAngleLowGear, int servoAngleHighGear)
    {
        shifter = new Servo(servoPin);
        angleL = servoAngleLowGear;
        angleH = servoAngleHighGear;
        gearLastState = false;
        gearLastRequested = false;
        timeLastRequested = Timer.getFPGATimestamp() - SwerveConstants.SHIFTER_SHIFT_TIME;  // Start fully in this gear
    }
    
    /**
     * Shift. Safe to repeatedly call
     * @param useHighGear True: High gear, False: Low gear 
     */
    public void setGear(boolean useHighGear)
    {
        int angle = (useHighGear) ? angleH:angleL;
        
        shifter.setAngle(angle);
        
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
        if (Timer.getFPGATimestamp() - timeLastRequested > SwerveConstants.SHIFTER_SHIFT_TIME)
        {
            gearLastState = gearLastRequested;
        }
        
        return gearLastState;
    }
}