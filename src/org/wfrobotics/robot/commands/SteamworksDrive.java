package org.wfrobotics.robot.commands;

import org.wfrobotics.reuse.commands.drive.swerve.DriveSwerve;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.Commands;

import edu.wpi.first.wpilibj.Timer;

public class SteamworksDrive extends DriveSwerve
{
    private final double ANGLE_OFF_MIN = Commands.INTAKE_OFF_ANGLE;
    private final double ANGLE_OFF_MAX = 180 - Commands.INTAKE_OFF_ANGLE;
    private final double directionOfIntake = 90;

    private double intakeLastOn;

    public SteamworksDrive()
    {
        super();
        requires(Robot.intakeSubsystem);
    }

    protected void initialized()
    {
        super.initialize();
        Robot.driveSubsystem.setGearHopper(true);
        intakeLastOn = Timer.getFPGATimestamp();
    }

    protected void execute()
    {
        super.execute();

        HerdVector lastVelocity = state.getRobotVelocity();
        double centeredOnIntake = lastVelocity.getAngle() + directionOfIntake;
        double now = Timer.getFPGATimestamp();
        double speed = 0;

        // Restart the intake timers whenever we move in that direction
        if(Math.abs(lastVelocity.getMag()) > .1 && centeredOnIntake > ANGLE_OFF_MIN && centeredOnIntake < ANGLE_OFF_MAX)
        {
            intakeLastOn = now;
        }

        // Keep the intakes for a while after we stop moving in that direction
        if(Robot.controls.getIntake() || now - intakeLastOn < Commands.INTAKE_OFF_TIMEOUT)
        {
            speed = 1;
        }

        log.debug("angleDifference", centeredOnIntake);
        log.debug("Intake", speed == 1);
        Robot.intakeSubsystem.setSpeed(speed);
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
