package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class WristToggle extends InstantCommand
{
    Wrist wrist = Wrist.getInstance();

    public WristToggle()
    {
        requires(wrist);
    }
    protected void initialize()
    {
        wrist.setPoppers(!wrist.state);
        wrist.setState(!wrist.state);
    }
}
