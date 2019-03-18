package org.wfrobotics.robot.commands.drive;

import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.RobotStateBase;
import org.wfrobotics.reuse.config.EnhancedIO;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

/** Turn robot to angle **/
public class TurnParallel extends Command
{
    private final boolean kOpenLoopBrake;

    protected final RobotStateBase state = EnhancedRobot.getState();
    protected final TankSubsystem drive = TankSubsystem.getInstance();
    protected final EnhancedIO io = EnhancedRobot.getIO();

    protected double heading;
    protected boolean gyroOk;

    public TurnParallel(double sensor1, double sensor2, double dist_apart)
    {
        requires(drive);
        kOpenLoopBrake = EnhancedRobot.getConfig().getTankConfig().OPEN_LOOP_BRAKE;
        // TODO DRL this needs to be in initialize, if we want in reuse, will need to be two DoubleSuppliers in constructor
        heading = drive.getGryo() + Math.toDegrees(Math.atan((sensor2 - sensor1) / dist_apart));
    }

    protected void initialize()
    {
        gyroOk = drive.isGyroOk();  // TODO Move to AutoMode, cancel parent group method?
        if (gyroOk)
        {
            drive.setBrake(true);
            drive.turnToHeading(heading);  // Extra robot iteration of progress
        }
    }

    protected boolean isFinished()
    {
        return !gyroOk || drive.onTarget() || io.isDriveOverrideRequested();
    }

    protected void end()
    {
        final boolean inTeleop = DriverStation.getInstance().isOperatorControl();
        if (inTeleop)
        {
            drive.setBrake(kOpenLoopBrake);
        }
    }
}