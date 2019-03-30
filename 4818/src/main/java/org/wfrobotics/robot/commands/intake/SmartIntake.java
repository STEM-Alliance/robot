package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.subsystems.Elevator;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.Link;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SmartIntake extends Command
{
    private static final double kLinkDownForCargoDegrees = 80.0;
    private static final double kSpeedCargoOut = 0.8;

    private final Intake intake = Intake.getInstance();
    private final Elevator elevator = Elevator.getInstance();
    private final Link link = Link.getInstance();
    private final SuperStructure superStructure = SuperStructure.getInstance();

    public SmartIntake()
    {
        requires(intake);
    }

    protected void execute()
    {
        autoHatch();
        autoCargo();
    }

    protected boolean isFinished()
    {
        return false;
    }

    private void autoHatch()
    {
        if (intake.hasAutoModeHatch())
        {
            // intake.setGrabber(true);  // Enable after they fix hatch wont stand up
        }
        else if (superStructure.getHasCargo())
        {
            // intake.setGrabber(true);
        }
        else if (superStructure.getHasHatch())
        {
            // intake.setGrabber(false);
        }
    }

    private void autoCargo()
    {
        final boolean isLinkDown = link.getPosition() > kLinkDownForCargoDegrees;
        final boolean isElevatorDown = elevator.getPosition() < 10.0;
        final boolean intakeCargoMode = isLinkDown && 
                                  isElevatorDown && 
                                  !superStructure.getHasCargo() ;
                                //   &&
                                //   !superStructure.getHasHatch()
        final double speed = (intakeCargoMode) ? kSpeedCargoOut : 0.0;
        SmartDashboard.putBoolean("isLinkDown", isLinkDown);
        SmartDashboard.putBoolean("isElevatorDown", isElevatorDown);
        SmartDashboard.putBoolean("intakeCargoMode", intakeCargoMode);
        intake.setCargoSpeed(speed);
    }
}
