package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WristToHeight extends InstantCommand
{
    double distance;

    /**
     * lifts to distance from bottom to top
     * @param distance 0(bottom) to 1 (top)
     */
    public WristToHeight(double distance)
    {
        requires(Robot.wrist);
        this.distance = distance;
    }

    protected void initialize()
    {
        SmartDashboard.putString("Wrist", this.getClass().getSimpleName());
        Robot.wrist.setPosition(distance);
    }
}
