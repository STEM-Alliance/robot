package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Rocket League the intake wheels */
public class IntakeManual extends Command
{
    protected static final double kWantsManualDeadband = .2;

    protected final IntakeSubsystem intake = Robot.intakeSubsystem;

    public IntakeManual()
    {
        requires(intake);
    }

    protected void initialize()
    {
        SmartDashboard.putString("Intake", "Manual");
    }

    protected void execute()
    {
        double speed = getManualIntakeSpeed();

        SmartDashboard.putNumber("Intake Wheels In", Robot.controls.getIntakeIn());
        SmartDashboard.putNumber("Intake Wheels Out", Robot.controls.getIntakeOut());

        intake.setMotor(speed);
    }

    protected boolean isFinished()
    {
        return wantsManualIntake();  // Must give up control for conditional command
    }

    public static boolean wantsManualIntake()  // TODO Seems to have a bug relative to conditional command where trigger values change but correct command not picked
    {
        double in = Robot.controls.getIntakeIn();
        double out = Robot.controls.getIntakeOut();
        return Math.abs(in - out) > kWantsManualDeadband;
    }

    private static double getManualIntakeSpeed()
    {
        return Robot.controls.getIntakeIn() - Robot.controls.getIntakeOut();  // TODO Should either controller do this?
    }
}
