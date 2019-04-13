package org.wfrobotics.robot.commands.wrist;

import edu.wpi.first.wpilibj.command.Command;
import org.wfrobotics.robot.subsystems.WristPneumatic;


public class WristPneumaticToggle extends Command
{
    private final WristPneumatic wrist = WristPneumatic.getInstance();

    public WristPneumaticToggle()
    {
        requires(wrist);
        setTimeout(.45);
    }

    public void initialize()
    {
        wrist.setWrist(!wrist.getWrist());
    }

    public boolean isFinished()
    {
        return true;
    }
}