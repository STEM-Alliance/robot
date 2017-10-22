package org.wfrobotics.robot.commands;

import org.wfrobotics.reuse.commands.drive.swerve.DriveFusion;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.reuse.utilities.Utilities;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.config.Commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SteamworksDrive extends CommandGroup
{
    private RobotState state = RobotState.getInstance();
    private IntakeSetup intake;

    private double intakeLastOn;

    public SteamworksDrive()
    {
        intake = new IntakeSetup(false);

        addParallel(intake);
        addSequential(new DriveFusion());
    }

    protected void initialized()
    {
        Robot.driveSubsystem.setGearHopper(true);
    }

    protected void execute()
    {
        HerdVector lastVelocity = state.getRobotVelocity();
        double angleDifference = lastVelocity.getAngle();
        double vectorMag = lastVelocity.getMag();
        boolean intakeOn;

        angleDifference = -(angleDifference - 90);
        angleDifference = Utilities.wrapToRange(angleDifference, -180, 180);

        // Restart the intake timers whenever we move in that direction
        if(Math.abs(vectorMag) > .1 &&
                angleDifference > Commands.INTAKE_OFF_ANGLE &&
                angleDifference  < (180 - Commands.INTAKE_OFF_ANGLE))
        {
            intakeLastOn = Timer.getFPGATimestamp();
        }

        if(Robot.controls.getIntake())
        {
            intakeOn = true;
        }
        else
        {
            // Keep the intakes for a while after we stop moving in that direction
            intakeOn = (Timer.getFPGATimestamp() - intakeLastOn) < Commands.INTAKE_OFF_TIMEOUT;
        }

        SmartDashboard.putNumber("angleDifference", angleDifference);
        SmartDashboard.putBoolean("intakeOn", intakeOn);

        intake.set(intakeOn);
    }

    protected void end()
    {
        Robot.intakeSubsystem.setSpeed(0);
    }

    protected void interrupted()
    {
        end();
    }
}
