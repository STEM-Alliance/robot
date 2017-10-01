package org.wfrobotics.robot.vision.messages;

import org.wfrobotics.robot.vision.util.VisionMessage;

/** Sets the co-processor's behavior (ex: set camera to use for vision) **/
public class CameraMode extends VisionMessage
{
    private final String mode;

    public CameraMode(int key)
    {
        mode = Integer.toString(key);
    }

    public String getType()
    {
        return "setSource";
    }

    public String getMessage()
    {
        return mode;
    }
}
