package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class WristOpenLoop extends Command
{
    private final Wrist wrist = Wrist.getInstance();
    private final IO io = IO.getInstance();

    public WristOpenLoop()
    {
        requires(wrist);
    }

    protected void execute()
    {
        final boolean inAuto = DriverStation.getInstance().isAutonomous();

        if (!inAuto)  // TODO ConditionalCommand cancels requirements
        {
            wrist.setOpenLoop(io.getWristStick());
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
