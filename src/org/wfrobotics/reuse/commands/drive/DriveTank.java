
package org.wfrobotics.reuse.commands.drive;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveTank extends Command
{
    public enum MODE {TANK, ARCADE, OFF}

    public final MODE mode;
    boolean backward;

    public DriveTank(MODE mode, boolean backward)
    {
        requires(Robot.driveSubsystem);
        this.mode = mode;
        this.backward = backward;
    }

    protected void execute()
    {
        double left = 0;
        double right = 0;

        if (mode == MODE.TANK)
        {
            double adjust = Robot.controls.tankIO.getThrottleSpeedAdjust();
            left = Robot.controls.tankIO.getL() * adjust;
            right = Robot.controls.tankIO.getR() * adjust;

            if(backward)
            {
                double temp;
                temp = left * -1.0;
                left = right * -1.0;
                right = temp;
            }

            // make sure both are non-zero before we try to lock straight
            if(Math.abs(left) > .01 && Math.abs(right) > .01)
            {
                // Assume we want to go straight if similar value
                if (Math.abs(left - right) < .15)
                {
                    double magnitudeAverage = Math.abs((left + right) / 2);
                    left = Math.signum(left) * magnitudeAverage;
                    right = Math.signum(right) * magnitudeAverage;
                }
            }
        }

        //Robot.driveSubsystem.driveTank(right, left); // TODO Couple to a dedicated tank subsystem
    }

    protected boolean isFinished()
    {
        return false;
    }
}
