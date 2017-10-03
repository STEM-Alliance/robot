package org.wfrobotics.robot.vision.commands;

import org.wfrobotics.robot.config.VisionMode;

public class GearMode extends SetCameraMode
{
    public int getMode()
    {
        return VisionMode.GEAR.getValue();
    }
}
