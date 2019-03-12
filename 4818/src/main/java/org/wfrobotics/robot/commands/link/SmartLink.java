package org.wfrobotics.robot.commands.link;

import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Link;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class SmartLink extends Command
{
    //    private final RobotState state = RobotState.getInstance();
    private final Link link = Link.getInstance();
    private final IO io = IO.getInstance();

    public SmartLink()
    {
        requires(link);
    }

    protected void execute()
    {
        final boolean inAuto = DriverStation.getInstance().isAutonomous();

        if (!inAuto)  // TODO ConditionalCommand cancels requirements
        {
            final double speed = io.getLinkUp() - io.getLinkDown();

            link.setOpenLoop(speed);
        }
        if (RobotState.getInstance().getMeasuredVelocity().dx > 10 )
        {
            link.setClosedLoop(80);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
