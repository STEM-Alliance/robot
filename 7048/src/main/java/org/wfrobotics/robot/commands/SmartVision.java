package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.Vision;

import edu.wpi.first.wpilibj.command.Command;

/** Selects the vision camera who's view is not obstructed by the other subsystems */
public final class SmartVision extends Command
{
    private final Vision vision = Vision.getInstance();

    public SmartVision()
    {
        requires(vision);
    }

    protected void execute()
    {
        vision.setCamera(elevatorCameraAvailable());
    }

    protected boolean isFinished()
    {
        return false;
    }

    private boolean elevatorCameraAvailable()
    {        
        //Our elevator doesn't move
        return true; 
    }
}