package org.wfrobotics.robot.auto;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PrintCommand extends Command
{
    private final String s;

    public PrintCommand(String s)
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
