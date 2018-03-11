package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class IntakeLiftToHeight extends InstantCommand
{
    double distance;

    /**
     * lifts to distance from bottom to top
     * @param distance 0(bottom) to 1 (top)
     */
    public IntakeLiftToHeight(double distance)
    {
        requires(Robot.wrist);
        this.distance = distance;
    }

    protected void initialize()
    {
        Robot.wrist.setIntakeLiftPosition(distance);
    }
}
