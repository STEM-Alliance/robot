package org.wfrobotics.prototype.commands;


import org.wfrobotics.prototype.Robot;
import org.wfrobotics.prototype.subsystems.ArmSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ArmPivotBase extends Command {


    protected void execute()
    {
        Robot.armSubsystem.spinUntilLimit(ArmSubsystem.isAtBaseCW(),
                ArmSubsystem.isAtBaseCCW(), Robot.controls.getXRotation() , ArmSubsystem.baseMotor);
        //speed may be needed to be multiplied  by a constant
    }
    protected boolean isFinished()
    {
        return false;
    }

}
