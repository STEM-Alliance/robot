package org.wfrobotics.prototype.commands;


import org.wfrobotics.prototype.Robot;
import org.wfrobotics.reuse.utilities.Utilities;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ArmPivotBase extends Command {

    public ArmPivotBase()
    {
        requires(Robot.armSubsystem);
    }


    protected void execute()
    {
        Utilities.spinUntilLimit(Robot.armSubsystem.isAtBaseCW(),
                Robot.armSubsystem.isAtBaseCCW(), Robot.controls.getXRotation(),
                Robot.armSubsystem.baseMotor);
        //speed may be needed to be multiplied  by a constant
    }
    protected boolean isFinished()
    {
        return false;
    }

}
