package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.utilities.HerdAngle;
import org.wfrobotics.reuse.utilities.PIDController;

/**
 * Absolute angle motor control PID. Corrects to setpoint or it's 180 degree mirror, whichever is closer.
 * @author Team 4818 WFRobotics
 */
public final class AnglePID
{
    private final PIDController pid;

    private boolean isReverseAngleCloser;
    private HerdAngle errorToCloserPath;

    public AnglePID(double p, double i, double d, double max)
    {
        pid = new PIDController(p, i, d, max);
        isReverseAngleCloser = false;
    }

    public String toString()
    {
        return errorToCloserPath.toString();
    }

    /** Calculate output to move towards setpoint, or its 180 degrees offset, via whichever of those two paths is closest */
    public double update(double setPoint, double sensorValue)
    {
        HerdAngle forwardError = new HerdAngle(setPoint - sensorValue);
        HerdAngle reversedError = forwardError.rotate(180);

        isReverseAngleCloser = Math.abs(reversedError.getAngle()) < Math.abs(forwardError.getAngle());
        errorToCloserPath = (isReverseAngleCloser) ? reversedError : forwardError;

        // TODO If we reverse, reset the derivative
        return pid.update(errorToCloserPath.getAngle());
    }

    public void updatePID(double p, double i, double d)
    {
        pid.setP(p);
        pid.setI(i);
        pid.setD(d);
    }

    /** Does taking the shortest path require turning the motor in the reverse direction? */
    public boolean isReverseAngleCloser()
    {
        return isReverseAngleCloser;
    }
}
