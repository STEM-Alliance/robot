import org.wfrobotics.robot.subsystems.ParellelLink;

import edu.wpi.first.wpilibj.command.Command;

public class LinkZero extends Command
{
    private final ParellelLink link = ParellelLink.getInstance();

    public LinkZero()
    {
        requires(link);
        setTimeout(3.0);
    }

    protected void execute()
    {
        link.setOpenLoop(-0.3);
    }

    protected boolean isFinished()
    {
        return link.hasZeroed() || isTimedOut();
    }

    protected void end()
    {
        link.setOpenLoop(0.0);
    }
}
