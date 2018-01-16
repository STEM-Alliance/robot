package org.wfrobotics.robot.commands.Vision;

import org.wfrobotics.reuse.commands.vision.VisionModeSet;
import org.wfrobotics.robot.config.VisionMode;

public class VisionModeCamera1 extends VisionModeSet
{
    public VisionMode getMode()
    {
        return VisionMode.CAMERA1;
    }
}
