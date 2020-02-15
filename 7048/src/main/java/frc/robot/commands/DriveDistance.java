package frc.robot.commands;

import frc.robot.subsystems.Drivetrain;
import frc.robot.EnhancedRobot;
import frc.robot.RobotStateBase;
import frc.robot.config.EnhancedIO;

import edu.wpi.first.wpilibj2.command.CommandBase;

/** Drive robot forwards/reverse a distance */
public class DriveDistance extends CommandBase
{
    protected final RobotStateBase state = EnhancedRobot.getState();
    protected final Drivetrain drive;
    protected final EnhancedIO io = EnhancedRobot.getIO();

    private double settledSamples;  // Allow PID to work, TODO have subsystem latch once reached instead
    protected double desired;
    protected final double tol = .05;

    public DriveDistance(Drivetrain drive, double inchesForward)
    {
        addRequirements(drive);
        this.drive = drive;
        desired = inchesForward;
    }

    public void initialize() {
        drive.setBrake(true);
        settledSamples = 0;
    }

    public void execute() {
        drive.driveDistance(desired);
    }

    public boolean isFinished() {
        final double error = (desired - state.getDistanceDriven()) / desired;

        if (Math.abs(error) < tol) {
            settledSamples++;
        }
        else {
            settledSamples = 0;
        }
        return settledSamples > 5  || io.isDriveOverrideRequested();
    }
}
