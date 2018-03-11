package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ExampleForwardCommand extends Command
{
    public ExampleForwardCommand()
    {
        requires(Robot.prototypeSubsystem);
    }

    protected void initialize()
    {
        SmartDashboard.putString("MP Command", "Init");
        Robot.prototypeSubsystem.start();
    }

    protected void execute()
    {
        SmartDashboard.putString("MP Command", "Execute");
        Robot.prototypeSubsystem.update();
    }

    protected boolean isFinished()
    {
        return Robot.prototypeSubsystem.isDone();
    }

    protected void end()
    {
        SmartDashboard.putString("MP Command", "End");
        Robot.prototypeSubsystem.manual(0);
    }
}