package org.wfrobotics.commands.drive;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveZeroGyro extends Command {

    public DriveZeroGyro()
    {
        requires(Robot.driveSubsystem);
    }
    
    protected void initialize()
    {
        Robot.driveSubsystem.gyroZero();
    }
    
    @Override
    protected boolean isFinished()
    {
        return true;
    }

}
