package org.wfrobotics.robot.vision.commands;

import org.wfrobotics.robot.vision.CameraServer;
import org.wfrobotics.robot.vision.messages.CameraMode;

import edu.wpi.first.wpilibj.command.InstantCommand;

public abstract class SetCameraMode extends InstantCommand
{
    public abstract int getMode();

    protected void initialize()
    {
        int mode = getMode();
        CameraServer.getInstance().send(new CameraMode(mode));
    }
}
