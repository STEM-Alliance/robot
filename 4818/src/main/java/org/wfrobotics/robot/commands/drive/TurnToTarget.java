package org.wfrobotics.robot.commands.drive;

import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.reuse.utilities.ConsoleLogger;
import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.config.EnhancedIO;
import org.wfrobotics.robot.subsystems.SuperStructure;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.wfrobotics.robot.RobotState;

/** Turn until reaching the target, or get to the expected heading it should be at **/
public class TurnToTarget extends Command
{
    SuperStructure state = SuperStructure.getInstance();
    RobotState robotstate = RobotState.getInstance();
    protected final TankSubsystem drive = TankSubsystem.getInstance();
    protected final EnhancedIO io = EnhancedRobot.getIO();
    protected boolean targetAvailable = false;
    double tol = 3;

    public TurnToTarget()
    {
        requires(drive);
    }

    protected void initialize()
    {
        if (!state.getTapeInView())
        {
            ConsoleLogger.warning("Cannot turn to target, target not in view");
            targetAvailable = false;
        }
        else if (drive.isGyroOk())
        {
            drive.setBrake(true);
            doTurn();
        }
    }
       protected boolean isFinished()
       {
           /**
            *  ------if the target isn't in the frame
            *  if the robot is within tol of the target
            */
           if (state.getTapeInView())
           {
               if ( Math.abs(drive.getGryo() - state.getTapeYaw()) < tol)
               {
                   return true;
               }
               return false;
           }
           return true;
       }

    // protected boolean isFinished()
    // {
    //     return !targetAvailable || drive.onTarget() || io.isDriveOverrideRequested();
    // }

    private void doTurn()
    {
        double angle = drive.getGryo() + state.getTapeYaw();
        SmartDashboard.putNumber("Vision Heading", angle);
        drive.turnToHeading(angle);
    }
}
