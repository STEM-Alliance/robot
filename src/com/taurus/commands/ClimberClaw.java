package com.taurus.commands;

import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class ClimberClaw extends Command {

    boolean release;
    
    public ClimberClaw(boolean release)
    {
        this.release = release;
    }
    
    @Override
    protected void initialize()
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void execute()
    {
        // TODO Auto-generated method stub
        if(release)
        {
            Robot.climberClawSubsystem.release();
        }
        else
        {
            Robot.climberClawSubsystem.hold();
        }
    }

    @Override
    protected boolean isFinished()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void end()
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void interrupted()
    {
        // TODO Auto-generated method stub

    }

}
