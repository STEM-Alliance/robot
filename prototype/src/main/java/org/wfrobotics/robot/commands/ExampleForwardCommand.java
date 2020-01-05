package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.ExampleSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/** TODO: comment what this Command does */
// TODO rename me (right click -> refactor -> rename)
public class ExampleForwardCommand extends Command
{
    // TODO create any objects this Command needs to remember for later
    double speed;

    
    public ExampleForwardCommand(double speed)  // TODO pass any parameters needed to setup the command
    {
        requires(ExampleSubsystem.getInstance());
        // TODO save off any objects needed to remember for later
        this.speed = speed;
    }

    protected void initialize()
    {
        // TODO do anything this Command needs to happen ONCE
    }

    protected void execute()
    {
        // TODO do anything this Command needs to happen REPEATEDLY until it's finished
        ExampleSubsystem.getInstance().setSpeed(speed);

    }

    protected boolean isFinished()
    {
        // TODO return 'true' whenever this Command has COMPLETED it's purpose
        ExampleSubsystem.getInstance().setSpeed(0);
        
        return false;
    }
}