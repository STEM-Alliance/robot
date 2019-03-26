package org.wfrobotics.robot.commands.climb;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Climb;

import edu.wpi.first.wpilibj.command.Command;

public class PullForward extends Command
{
    private final Climb climb = Climb.getInstance();
    private final IO io = IO.getInstance();

    public PullForward()
    {
        requires(climb);
    }
    protected void execute()
    {
        final double speed = io.getClimbArmsStick();
        climb.setOpenLoop(speed);

        climb.setPullers(1.0);
    }
    protected void end()
    {
        climb.setPullers(0.0);
    }
    protected boolean isFinished()
    {
        return false;
    }
}
