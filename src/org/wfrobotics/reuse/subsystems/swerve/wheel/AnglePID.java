package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.utilities.HerdAngle;
import org.wfrobotics.reuse.utilities.PIDController;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Absolute angle motor control PID. Corrects to setpoint or it's 180 degree mirror, whichever is closer.
 * @author Team 4818 WFRobotics
 */
public final class AnglePID
{
    private final PIDController pid;

    private boolean isReverseAngleCloser;
    private HerdAngle errorToCloserPath;

    public AnglePID(double p, double i, double d)
    {
        pid = new PIDController(p, i, d, 1);
        isReverseAngleCloser = false;
    }

    public String toString()
    {
        return errorToCloserPath.toString();
    }

    /** Calculate output to move towards setpoint, or its 180 degrees offset, via whichever of those two paths is closest */
    public double update(double setPoint, double sensorValue)
    {
        SmartDashboard.putNumber("sense", sensorValue);
        HerdAngle forwardError = new HerdAngle(setPoint - sensorValue);
        HerdAngle reversedError = forwardError.rotate(180);

        isReverseAngleCloser = Math.abs(reversedError.getAngle()) < Math.abs(forwardError.getAngle());
        errorToCloserPath = (isReverseAngleCloser) ? reversedError : forwardError;

        // TODO If we reverse, reset the derivative
        double output = pid.update(forwardError.getAngle());
        output = output - .5;  // PID wont go negative
        SmartDashboard.putNumber("set", setPoint);
        SmartDashboard.putNumber("e", errorToCloserPath.getAngle());
        SmartDashboard.putNumber("o", output);
        return output;
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
