
package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AutoNone extends Command 
{
    final double speedR;
    final double speedL;
    boolean tractionEnabled;
    boolean gyroEnabled;
    
    /**
     * Default to not driving (speed equals zero)
     */
    public AutoNone()
    {
        requires(Robot.tankDriveSubsystem);
        speedR = 0;
        speedL = 0;
    }
    
    protected void initialize()
    {
    }

    protected void execute() 
    {
        Robot.tankDriveSubsystem.driveRaw(0, 0);
    }

    protected boolean isFinished()
    {
        return true;
    }

    protected void end() 
    {
        Robot.tankDriveSubsystem.driveRaw(0, 0);
    }

    // subsystems is scheduled to run
    protected void interrupted()
    {
        
    }
}
