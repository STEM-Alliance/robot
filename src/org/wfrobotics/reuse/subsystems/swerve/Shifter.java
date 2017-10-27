package org.wfrobotics.reuse.subsystems.swerve;


import org.wfrobotics.reuse.subsystems.swerve.chassis.Config;
import org.wfrobotics.robot.config.RobotMap;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;

/** Swerve Drive Shifter that shifts each Swerve Wheel */
public class Shifter
{
    private final int[] TOP = new int[4];
    private final int[] BOTTOM = new int[4];

    private final Servo[] shifter = new Servo[4];

    private boolean state;
    private boolean lastRequest;
    private double timeLastRequest;

    public Shifter()
    {
        for (int index = 0; index < 4; index++)
        {
            int halfRange = (Config.SHIFTER_INVERT[index]) ? -Config.SHIFTER_RANGE / 2 : Config.SHIFTER_RANGE / 2;

            shifter[index] = new Servo(RobotMap.PWM_SWERVE_SHIFT_SERVOS[index]);

            TOP[index] = Config.SHIFTER_VALS[index] + halfRange;
            BOTTOM[index] = Config.SHIFTER_VALS[index] - halfRange;
        }

        state = false;
        lastRequest = state;
        timeLastRequest = Timer.getFPGATimestamp() - Config.SHIFTER_SHIFT_TIME;  // Start fully in this gear
    }

    /** Shift. Safe to repeatedly call */
    public void setGear(boolean useHighGear)
    {
        shifter[0].setAngle((useHighGear) ? TOP[0] : BOTTOM[0]);
        shifter[1].setAngle((useHighGear) ? TOP[1] : BOTTOM[1]);
        shifter[2].setAngle((useHighGear) ? TOP[2] : BOTTOM[2]);
        shifter[3].setAngle((useHighGear) ? TOP[3] : BOTTOM[3]);

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