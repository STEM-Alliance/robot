package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.reuse.commands.DriveCommand;

public class ToggleConstAccel extends DriveCommand
{
    static boolean toggleDirection = true;

    private final double distance;
    private double settledSamples;

    public ToggleConstAccel(double inchesForward)
    {
        requires(Robot.driveService.getSubsystem());
        distance = inchesForward;
        settledSamples = 0;
    }

    protected void initialize()
    {
        super.initialize();
        Robot.driveService.driveDistanceInit(getTargetInches());
    }

    protected void execute()
    {
        Robot.driveService.driveDistanceUpdate();
    }

    protected boolean isFinished()
    {
        double remainingInches = getTargetInches() - state.robotDistanceDriven;

        if (Math.abs(remainingInches) < Math.abs(getTargetInches() * .05))
        {
            settledSamples++;
        }
        else
        {
            settledSamples = 0;
        }
        return settledSamples > 20;  // Tight tolerances necessary in testing
    }

    protected void end()
    {
        toggleDirection = !toggleDirection;
    }

    private double getTargetInches()
    {
        return distance * (toggleDirection ? 1 : -1);
    }
}