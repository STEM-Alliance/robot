package org.wfrobotics.reuse.subsystems.vision.messages;

public abstract class VisionMessage
{
    public abstract String toString();
    
    /**
     * Get a decoded/human-readable string
     * @return
     */
    public abstract String Decoded();
}
