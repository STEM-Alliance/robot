package org.wfrobotics.robot.auto;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class Drive extends Command
{
    public Drive()
    {
        requires(Robot.driveSubsystem);
    }
    @Override
    protected boolean isFinished()
    {
        // TODO Auto-generated method stub
        return false;
    }

}
