package org.wfrobotics.robot.commands.ParellelLink;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.ParellelLink;

import edu.wpi.first.wpilibj.command.Command;

public class DumbLink extends Command
{
    ParellelLink link = ParellelLink.getInstance();

    public DumbLink()
    {
        requires(link);
    }
    protected void execute()
    {
        link.setPrecent(IO.getInstance().getLinkStick());
    }
    protected boolean isFinished()
    {
        return false;
    }

}
