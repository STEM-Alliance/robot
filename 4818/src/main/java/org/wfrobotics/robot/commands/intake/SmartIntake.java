package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.Link;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SmartIntake extends Command
{
    private final Intake intake = Intake.getInstance();

    public SmartIntake()
    {
        requires(intake);
    }

    protected void execute()
    {
        if (SuperStructure.getInstance().getHasHatch())
        {
            intake.setGrabber(true);
        }

        boolean intakeCargoMode = Link.getInstance().getPosition() > 98 &&
                                  !SuperStructure.getInstance().getHasCargo() &&
                                  !SuperStructure.getInstance().getHasHatch();

        SmartDashboard.putBoolean("Intake Cargo mode?", intakeCargoMode);
        if (intakeCargoMode)
        {
            intake.setCargoSpeed(1.0);
        } else { intake.setCargoSpeed(0.0); }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
