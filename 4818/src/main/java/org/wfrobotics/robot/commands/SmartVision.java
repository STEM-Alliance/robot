package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.Elevator;
import org.wfrobotics.robot.subsystems.Link;
import org.wfrobotics.robot.subsystems.Vision;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Selects the vision camera who's view is not obstructed by the other subsystems */
public final class SmartVision extends Command
{
    private final Elevator elevator = Elevator.getInstance();
    private final Link link = Link.getInstance();
    private final Vision vision = Vision.getInstance();

    public SmartVision()
    {
        requires(vision);
    }

    protected void execute()
    {
        if (elevator.hasZeroed() && link.hasZeroed())
        {
            vision.setCamera(elevatorCameraAvailable());
        }
        else { vision.setCamera(false);}
    }

    protected boolean isFinished()
    {
        return false;
    }

    private boolean elevatorCameraAvailable()
    {       
        final double height = elevator.getPosition();
        final double angle = link.getPosition();
        
        final boolean elevatorIsDown = height < 10.0;
        final boolean elevatorIsUp = height > 10.0;
        final boolean linkNotToShiny = angle > 60.0;
        SmartDashboard.putBoolean("Elevator down", elevatorIsDown);
        SmartDashboard.putBoolean("Elevator up", elevatorIsUp);
        SmartDashboard.putBoolean("link up", linkNotToShiny);

        boolean ret =  (elevatorIsDown && linkNotToShiny);
        SmartDashboard.putBoolean("elevator camera enable", ret);
        return ret;
    }
}