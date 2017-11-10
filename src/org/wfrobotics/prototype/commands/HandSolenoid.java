package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class HandSolenoid extends Command {
    boolean in;

    public HandSolenoid(boolean in)
    {
        requires(Robot.handSubsystem);

        this.in = in;
        // TODO Auto-generated constructor stub
    }
    public void initialize()
    {
        Robot.handSubsystem.moveHand(in);
    }

    @Override
    protected boolean isFinished()
    {
        // TODO Auto-generated method stub
        return true;
    }

}
