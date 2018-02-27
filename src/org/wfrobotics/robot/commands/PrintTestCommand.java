package org.wfrobotics.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PrintTestCommand extends Command
{
    private final String s;

    public PrintTestCommand(String s)
    {
        this.s  = s;
    }

    protected void initialize()
    {
        SmartDashboard.putString("Test", s);
    }

    protected boolean isFinished()
    {
        return true;
    }
}
