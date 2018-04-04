package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.commands.wrist.WristToHeight;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLiftToBottom extends CommandGroup
{
    public AutoLiftToBottom()
    {
        this.addParallel((new WristToHeight(-1.0)));  // SLAM SLAM
        this.addSequential(new LiftToHeight(LiftHeight.Intake.get()));
    }
}
