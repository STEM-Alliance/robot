package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.reuse.utilities.Utilities;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ArmPivotElbow extends Command {

    double speed;


    public ArmPivotElbow()
    {
        requires(Robot.armSubsystem);
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    protected void execute()
    {
      Utilities.spinUntilLimit(Robot.armSubsystem.isAtTop(),Robot.armSubsystem.isAtBottom(),
                .5 * (Robot.controls.getRightY()), Robot.armSubsystem.elbowMotor1);
        //speed may be needed to be multiplied  by a constant
    }

    protected boolean isFinished()
    {
        return false;
    }

    protected void end()
    {
    }
    protected void interrupted()
    {
    }
}
