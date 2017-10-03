package org.wfrobotics.robot.vision.commands;

import org.wfrobotics.robot.config.VisionMode;

public class ShooterMode extends SetCameraMode
{
    public int getMode()
    {
        return VisionMode.SHOOTER.getValue();
    }
}
