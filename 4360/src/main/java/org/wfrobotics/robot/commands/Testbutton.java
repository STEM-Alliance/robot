package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.config.IO;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.command.Command;

public class Testbutton extends Command
{
    private final IO io = IO.getInstance();

    public Testbutton()
    {
    }

    protected void execute()
    {
        SmartDashboard.putBoolean("button pressed", true);
    }

    protected void end()
    {
        SmartDashboard.putBoolean("button pressed", false);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
