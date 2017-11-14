package org.wfrobotics.robot.commands;

import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.messages.VisionMessageConfig;

import edu.wpi.first.wpilibj.command.InstantCommand;

public abstract class VisionModeSet extends InstantCommand
{
    public abstract int getMode();

    protected void initialize()
    {
        int mode = getMode();
        CameraServer.getInstance().SetConfig(new VisionMessageConfig(mode));
    }
}
