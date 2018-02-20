package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SmartOutake extends Command
{
    protected final RobotState state = RobotState.getInstance();
    protected final IntakeSubsystem intake = Robot.intakeSubsystem;

    public SmartOutake()
    {
        requires(intake);
    }

    protected void initialize()
    {
        SmartDashboard.putString("Intake", "Smart Out");
    }

    protected void execute()
    {
        // Robot has a cube and the lift is above x inches
        if (state.liftHeightInches >= 10)
        {
            // Getting passed the complience wheels
            if (state.intakeDistance < 6)
            {
                intake.setMotor(0.2);
            }
            else  // When it is passed the wheels open up the Jaws
            {
                intake.setHorizontal(true);
            }
        }
    }

    protected boolean isFinished()
    {
        return !state.robotHasCube;
    }
}
