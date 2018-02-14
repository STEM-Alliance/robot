package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Pull in the cube purely based on sensors */
public class SmartIntake extends CommandGroup
{
    protected final RobotState state = RobotState.getInstance();
    protected final IntakeSubsystem intake = Robot.intakeSubsystem;

    /**
     * This command gets the distance from the subsystem and based on that distance
     * drive the motors at different speeds
     */
    public SmartIntake()
    {
        requires(intake);
    }

    protected void initialize()
    {
        SmartDashboard.putString("Intake", "Smart");
    }

    protected void execute()
    {
        double distanceToCube = state.intakeSensorReadout;

        autoIntake(distanceToCube);
        autoJaws(distanceToCube);
    }

    protected boolean isFinished()
    {
        return !IntakeManual.wantsManualIntake();  // Must give up control for conditional command
    }

    private void autoIntake(double distanceToCube)
    {
        double speed = 0.0;

        // TODO Better off with PID on wheel speed while in the range that we do non-zero wheel speed? Analog Talon position control?

        if (distanceToCube > 8 && distanceToCube < 42)  // TODO Need to move sensor, otherwise we stall motors
        {
            speed = -0.5;  // TODO Find ideal intake speed, put in RobotMap
        }

        // TODO After it's in for a little bit, it's SUPER effective to pulse the cube out a sec then back in to orient it
        //      This would also help us not stall if we don't drive wheels after that pulse. Or we move the distance sensor back enough to always be in a valid range.

        intake.setMotor(speed);
    }

    private void autoJaws(double distanceToCube)
    {
        String isJawtomated = "No";

        if (distanceToCube < 32)
        {
            intake.setHorizontal(false);  // Can't always set, otherwise we chatter?
            isJawtomated = "Close";
        }
        else if (distanceToCube > 32 && distanceToCube < 62)  // TODO find ideal range to be auto-opened, put in RobotMap or use robot state
        {
            intake.setHorizontal(true);
            isJawtomated = "Open";
        }

        SmartDashboard.putString("Jawtomatic", isJawtomated);
    }
}
