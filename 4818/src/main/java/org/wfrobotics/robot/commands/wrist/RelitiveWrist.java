package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.subsystems.Link;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.Command;

public class RelitiveWrist extends Command
{
    private final Wrist wrist = Wrist.getInstance();
    private final Link link = Link.getInstance();

    public RelitiveWrist()
    {
        requires(wrist);
    }

    protected void execute()
    {
        if (wrist.hasZeroed() && link.hasZeroed())
        {
            final double linkPos = link.getPosition();
            final double wristPos = wrist.getPosition();
            final double delta = linkPos - linkPos;

            if (Math.abs(linkPos) > 0.0)
            {
                wrist.setClosedLoop(wristPos - delta);
            }
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
