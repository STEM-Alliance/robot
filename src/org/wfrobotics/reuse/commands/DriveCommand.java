package org.wfrobotics.reuse.commands;

import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;

public abstract class DriveCommand extends Command
{
    protected final HerdLogger log = new HerdLogger("DriveCommand");
    protected final RobotState state = RobotState.getInstance();

    protected void initialize()
    {
        log.info("Drive", this.getClass().getSimpleName());
    }
}
