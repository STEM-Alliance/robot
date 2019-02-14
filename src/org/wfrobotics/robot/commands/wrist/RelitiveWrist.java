package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.subsystems.Link;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.Command;

public class RelitiveWrist extends Command
{
    Wrist wrist = Wrist.getInstance();
    Link link = Link.getInstance();

    public RelitiveWrist()
    {
        requires(wrist);
    }

    double linkPos = 0;
    protected void execute()
    {
        if (wrist.hasZeroed() && link.hasZeroed())
        {
            if (Math.abs(link.getPosition()) > 0.0)
            {
                double deltaLinkPos = linkPos - link.getPosition();
                wrist.setClosedLoop(wrist.getPosition() - deltaLinkPos);
            }
            linkPos = link.getPosition();
        }
    }
    protected boolean isFinished()
    {
        return false;
    }
}
