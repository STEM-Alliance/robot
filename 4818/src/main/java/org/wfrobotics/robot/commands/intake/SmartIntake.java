package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.Link;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.command.Command;

public class SmartIntake extends Command
{
    private static final double kSpeedCargoOut = 0.8;

    private final Intake intake = Intake.getInstance();
    private final Link link = Link.getInstance();
    private final SuperStructure superStructure = SuperStructure.getInstance();

    public SmartIntake()
    {
        requires(intake);
    }

    protected void initialize()
    {
        intake.setGrabber(true);
    }

    protected void execute()
    {
        final boolean isLinkDown = link.getPosition() > 145.0;
        final boolean intakeCargoMode = isLinkDown && 
                                  !superStructure.getHasCargo() &&
                                  !superStructure.getHasHatch();
        final double speed = (intakeCargoMode) ? kSpeedCargoOut : 0.0;

        intake.setCargoSpeed(speed);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
