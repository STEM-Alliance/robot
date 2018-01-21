package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class Shifting extends Command {
    boolean high = true;

    public Shifting()
    {
        requires(Robot.shiftingSubsystem);
    }

    public Shifting(boolean high)
    {
        requires(Robot.shiftingSubsystem);
        this.high = high;
        // TODO Auto-generated constructor stub
    }

    public void initialize()
    {
        Robot.shiftingSubsystem.shiftingSet(high);
        // TODO Auto-generated constructor stub
    }


    @Override
    protected boolean isFinished()
    {
        // TODO Auto-generated method stub
        return false;
    }

}
