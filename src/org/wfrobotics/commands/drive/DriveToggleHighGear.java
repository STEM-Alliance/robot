package org.wfrobotics.commands.drive;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveToggleHighGear extends Command {

    public DriveToggleHighGear() 
    {
        requires(Robot.driveSubsystem);
    }
    
    protected void initialize()
    {
        Robot.driveSubsystem.setGearHigh(!Robot.driveSubsystem.getGearHigh());
    }
    
    @Override
    protected boolean isFinished()
    {
        return true;
    }

}
