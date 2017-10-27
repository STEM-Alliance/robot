package org.wfrobotics.reuse.subsystems.swerve;

import org.wfrobotics.reuse.subsystems.swerve.wheel.Config;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;

/** Swerve Drive Shifter that shifts each Swerve Wheel */
public class Shifter
{
    private final int TOP;
    private final int BOTTOM;

    private final Servo shifter;

    private boolean state;
    private boolean lastRequest;
    private double timeLastRequest;

    public Shifter(int servoPin, int angleMidway, int angleRange, boolean invert)
    {
        int halfRange = (invert) ? -angleRange / 2 : angleRange / 2;

        shifter = new Servo(servoPin);
        TOP = angleMidway + halfRange;
        BOTTOM = angleMidway - halfRange;
        state = false;
        lastRequest = state;
        timeLastRequest = Timer.getFPGATimestamp() - Config.SHIFTER_SHIFT_TIME;  // Start fully in this gear
    }

    /** Shift. Safe to repeatedly call */
    public void setGear(boolean useHighGear)
    {
        shifter.setAngle((useHighGear) ? TOP : BOTTOM);

        // Start a transition if actually changing - allows for repeated calls without resetting timer
        if(lastRequest != useHighGear)
        {
            lastRequest = useHighGear;
            timeLastRequest = Timer.getFPGATimestamp();
        }
    }

    /** Current gear the shifter is in, determined by last state and time since state transition */
    public boolean isHighGear()
    {
        if (Timer.getFPGATimestamp() - timeLastRequest > Config.SHIFTER_SHIFT_TIME)
        {
            state = lastRequest;
        }
        return state;
    }
}