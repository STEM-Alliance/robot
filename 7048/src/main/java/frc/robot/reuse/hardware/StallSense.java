package frc.robot.reuse.hardware;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Timer;

/**
 * Monitors if a motor is stalled, meaning it's <b>speed is zero</b>.
 *
 * <p>Stalled electric motors produce a lot of <i>rotational force called torque</i>.
 * Doing so continuously draws a <b>large amount of current</b> and heats up the motor. Sometimes this
 * can even damage or burn out the motor! This class checks if the current draw is "big" for long
 * enough that we are pretty sure the motor is stalled.
 *
 * <p>When stalled, the motor is one of two conditions:
 * 1) It <b>cannot produce enough torque</b> to move. Ex: Our range of motion is being mechanically stopped.
 * 2) It is being <b>commanded to "hold" this position</b>. Ex: We are intentionally holding an arm out horizontally.
 *
 * @author STEM Alliance of Fargo Moorhead
 */
public class StallSense
{
    private final double kStallCurrentMin;
    private final double kStallTimeMin;

    private final TalonSRX talon;
    private double stallStarted;

    public StallSense(TalonSRX talon, double stallMinAmps, double stallMinSeconds)
    {
        this.talon = talon;
        kStallCurrentMin = stallMinAmps;
        kStallTimeMin = stallMinSeconds;
        stallStarted = Double.POSITIVE_INFINITY;
    }

    public boolean isStalled()
    {
        boolean stalled = false;

        if (talon.getOutputCurrent() < kStallCurrentMin)
        {
            stallStarted = Double.POSITIVE_INFINITY;
        }
        else if (stallStarted == Double.POSITIVE_INFINITY)
        {
            stallStarted = Timer.getFPGATimestamp();
        }
        else if (Timer.getFPGATimestamp() - stallStarted > kStallTimeMin)
        {
            stalled = true;
        }

        return stalled;
    }
}
