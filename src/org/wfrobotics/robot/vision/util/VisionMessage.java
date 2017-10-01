package org.wfrobotics.robot.vision.util;

/** A message sent to, or received from, the vision co-processor **/
public abstract class VisionMessage
{
    public abstract String getType();
    public abstract String getMessage();

    public String toString()
    {
        return String.format("%s,%s", getType(), getMessage());
    }

    public byte[] toBytes()
    {
        return toString().getBytes();
    }
}
