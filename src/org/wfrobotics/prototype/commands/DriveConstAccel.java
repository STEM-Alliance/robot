package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.reuse.commands.DriveCommand;

public class DriveConstAccel extends DriveCommand
{
    private final double distance;
    private double settledSamples;

    public DriveConstAccel(double inchesForward)
    {
        requires(Robot.driveService.getSubsystem());
        distance = inchesForward;
        settledSamples = 0;
    }

    protected void initialize()
    {
        super.initialize();
        Robot.driveService.driveDistanceUpdate();
        Robot.driveService.driveDistanceInit(distance);
    }

    protected void execute()
    {
        Robot.driveService.driveDistanceInit(distance);
    }

    protected boolean isFinished()
    {        double remainingInches = distance - state.robotDistanceDriven;

    if (Math.abs(remainingInches) < Math.abs(distance * .001))
    {
        settledSamples++;
    }
    else
    {
        settledSamples = 0;
    }
    return settledSamples > 10;  // Tight tolerances necessary in testing
    }
}
