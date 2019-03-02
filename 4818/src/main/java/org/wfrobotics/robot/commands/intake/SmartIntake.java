package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Intake;
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
        SmartDashboard.putBoolean("ran this one this time", false);
        if (SuperStructure.getInstance().getHasHatch())
        {
            intake.setGrabber(true);
        }
        else
        {
            intake.setGrabber(false);
        }

    }

    protected boolean isFinished()
    {
        return false;
    }
}
