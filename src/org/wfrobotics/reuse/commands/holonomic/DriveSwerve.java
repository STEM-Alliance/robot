
package org.wfrobotics.reuse.commands.holonomic;

import org.wfrobotics.reuse.commands.DriveCommand;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.Drive;

/** Teleop standard Herd swerve. Control is field relative. **/
public class DriveSwerve extends DriveCommand
{
    public static boolean FIELD_RELATIVE = true;  // Toggle with button, need if gyro breaks

    private double highVelocityStart;

    public DriveSwerve()
    {
        requires(Robot.driveService.getSubsystem());
    }

    protected void initialize()
    {
        super.initialize();
        highVelocityStart = timeSinceInitialized();
    }

    protected void execute()
    {
        HerdVector speedRobot = Robot.controls.swerveIO.getVelocity();
        double speedRotation = Robot.controls.swerveIO.getRotation();
        double robotHeading = state.robotHeading;

        if (Drive.AUTO_SHIFT_ENABLE)
        {
            boolean gear;

            if (speedRobot.getMag() < Drive.AUTO_SHIFT_SPEED)  // Allow high gear to "kick in" after AUTO_SHIFT_SPEED seconds of high speed
            {
                highVelocityStart = timeSinceInitialized();
            }
            gear = timeSinceInitialized() - highVelocityStart > Drive.AUTO_SHIFT_TIME && speedRobot.getMag() > Drive.AUTO_SHIFT_SPEED;

            Robot.driveService.setGear(gear);
        }

        // TODO should mag squared move here? Removed from chassis because all drive commands shouldn't want, just joystick driving, right?
        //velocity = velocity.scale(velocity);  // Square mag - Finer control at low speed

        log.info("Robot Relative", speedRobot);
        if (FIELD_RELATIVE)
        {
            speedRobot = speedRobot.rotate(-robotHeading);
        }
        log.info("Field Relative", speedRobot);
        Robot.driveService.driveWithHeading(new SwerveSignal(speedRobot, speedRotation, robotHeading));
    }

    protected boolean isFinished()
    {
        return false;
    }

    protected void end()
    {
        Robot.driveService.driveWithHeading(new SwerveSignal(new HerdVector(0, 0)));
    }
}
