package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AutoDrive2 extends Command
{
    double x;
    double y;
    double timeout;
    
    public AutoDrive2(double x, double y, double timeout)
    {
        requires(Robot.prototypeSubsystem);
        this.x = x;
        this.y = y;
        this.timeout = timeout;
    }
    
    protected void initialize()
    {
        setTimeout(timeout);
    }
    
    protected void execute()
    {
        Robot.prototypeSubsystem.setSpeed(x, y);
    }

    @Override
    protected boolean isFinished()
    {
        return isTimedOut();
    }
}
