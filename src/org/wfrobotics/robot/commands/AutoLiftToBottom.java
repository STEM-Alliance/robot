package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.commands.wrist.IntakeLiftToHeight;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLiftToBottom extends CommandGroup
{
    public AutoLiftToBottom()
    {
        this.addParallel((new IntakeLiftToHeight(.01)));  // Don't slam into chassis, PID to sensor zero rather than go past it with momentum
        this.addSequential(new LiftToHeight(LiftHeight.Intake.get()));
    }
}
