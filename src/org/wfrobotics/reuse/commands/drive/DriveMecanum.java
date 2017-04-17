package org.wfrobotics.reuse.commands.drive;

import org.wfrobotics.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;

public class DriveMecanum extends Command 
{
    public DriveMecanum() 
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize() 
    {
    }

    protected void execute() 
    {        
        //Robot.driveSubsystem.driveXY(-Robot.oi.mecanumOI.getX()*0.7, Robot.oi.mecanumOI.getY()*0.7, -Robot.oi.mecanumOI.getRotation()*0.7);
    }

    protected boolean isFinished() 
    {
        return false;
    }

    protected void end()
    {
        
    }

    protected void interrupted() 
    {
        
    }
}
