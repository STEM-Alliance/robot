package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.commands.wrist.WristToHeight;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLiftToBottom extends CommandGroup
{
    public AutoLiftToBottom()
    {
        // TODO Delay wrist until mostly down with Command that waits for state
        this.addParallel((new WristToHeight(0.0)));  // SLAM SLAM
        this.addSequential(new LiftToHeight(LiftHeight.Intake.get()));
    }
}
