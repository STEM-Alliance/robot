package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class WristToHeight extends InstantCommand
{
    private final Wrist wrist = Wrist.getInstance();
    double distance;

    /**
     * lifts to distance from bottom to top
     * @param distance 0(bottom) to 1 (top)
     */
    public WristToHeight(double distance)
    {
        requires(wrist);
        this.distance = distance;
    }

    protected void initialize()
    {
        wrist.setPosition(distance);
    }
}
