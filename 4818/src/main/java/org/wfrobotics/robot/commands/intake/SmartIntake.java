package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.Link;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SmartIntake extends Command
{
    private final Intake intake = Intake.getInstance();
    private final Link link = Link.getInstance();
    private final SuperStructure superStructure = SuperStructure.getInstance();

    public SmartIntake()
    {
        requires(intake);
    }

    protected void execute()
    {
        final boolean isLinkTooHigh = link.getPosition() < 10.0;
        final boolean isLinkDown = link.getPosition() > 145.0;
        final boolean intakeCargoMode = isLinkDown && 
                                  !superStructure.getHasCargo() &&
                                  !superStructure.getHasHatch();

        // Set Hatch Intake
        if (superStructure.getHasHatch() && !isLinkTooHigh)
        {
            intake.setGrabber(true);
        }

        // Set Cargo Intake
        if (intakeCargoMode)
        {
            intake.setCargoSpeed(0.8);
        }
        else 
        {
            intake.setCargoSpeed(0.0);
        }
        
        // SmartDashboard.putBoolean("Intake Cargo mode?", intakeCargoMode);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
