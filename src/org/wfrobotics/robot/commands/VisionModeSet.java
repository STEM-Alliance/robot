package org.wfrobotics.robot.commands;

import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.robot.vision.messages.CameraMode;

import edu.wpi.first.wpilibj.command.InstantCommand;

public abstract class VisionModeSet extends InstantCommand
{
    public abstract int getMode();

    protected void initialize()
    {
        int mode = getMode();
        CameraServer.getInstance().send(new CameraMode(mode));
    }
}
