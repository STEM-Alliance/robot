package org.wfrobotics.commands;

import edu.wpi.first.wpilibj.command.Command;

public class Lift extends Command {

    public Lift()
    {
        //requires(Robot.lifterSubsystem);
    }
    
    @Override
    protected void execute()
    {
        // TODO get the gear state, call set for the motor, move the lifter down if it's not had a gear for a durration of time //Robot.lifterSubsystem.set(goToTop);
    }

    @Override
    protected boolean isFinished()
    {
        return false;
    }
}
