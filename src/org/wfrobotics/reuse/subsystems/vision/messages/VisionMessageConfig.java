package org.wfrobotics.reuse.subsystems.vision.messages;

import java.util.ArrayList;

public class VisionMessageConfig extends VisionMessage
{
    private final int Version = 2;
    
    // Target camera source (index of array in the configuration file)
    public int TargetSource;
    // Streamer camera source (index of array in the configuration file)
    public int StreamerSource;
    // Array of booleans to enable/disable GRIP processing/target detection
    public ArrayList<Boolean> ProcessingEnable;

    /**
     * SUPER basic configuration message. This only enables 1 camera to run
     * at a time, and that GRIP processing/Target detection and streaming
     * are all done on the same camera
     * @param TargetSource
     */
    public VisionMessageConfig(int Source)
    {
        this(Source, Source);
    }
    
    /**
     * Basic configuration message with option to have different target and streamer
     * sources. This assumes that GRIP processing/target detection is only done
     * for the Target source camera.
     * @param TargetSource
     * @param StreamerSource
     */
    public VisionMessageConfig(int TargetSource, int StreamerSource)
    {
        this.TargetSource = TargetSource;
        this.StreamerSource = StreamerSource;
        this.ProcessingEnable = new ArrayList<>(2); //TODO get count from somewhere else?
        
        // set them all to false
        for (int i = 0; i < this.ProcessingEnable.size(); i++)
        {
            this.ProcessingEnable.set(TargetSource, false);
        }
        this.ProcessingEnable.set(TargetSource, true);
    }

    /**
     * Full configuration options. Set the Target and Streamer sources, and
     * individually select which are doing GRIP processing/target detection.
     * @Deprecated
     * This isn't currently advised, as doing GRIP processing on multiple camera
     * streams is highly resource intensive, and will introduce lag and low FPS.
     * @param TargetSource
     * @param StreamerSource
     * @param ProcessingEnable
     */
    @Deprecated
    public VisionMessageConfig(int TargetSource, int StreamerSource,
            ArrayList<Boolean> ProcessingEnable)
    {
        this.TargetSource = TargetSource;
        this.StreamerSource = StreamerSource;
        this.ProcessingEnable = ProcessingEnable;
    }

    /**
     * Get the raw message string
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(50);

        // Message is formatted as comma separated list of
        // MsgLength,Version,CameraSource,Width,Height,Exposure,Brightness,Saturation
        sb.append(Version).append(",");
        sb.append(TargetSource).append(",");
        sb.append(StreamerSource).append(",");
        sb.append(ProcessingEnable.size()).append(",");
        for (int i = 0; i < ProcessingEnable.size(); i++)
        {
            sb.append(ProcessingEnable.get(i) ? 1 : 0);
            
            if (i + 1 < ProcessingEnable.size())
                sb.append(",");
        }
        
        sb.insert(0, Integer.toString(sb.length()) + ",");
        
        return sb.toString();
    }
    
    /**
     * Get a decoded/human-readable string
     * @return
     */
    public String Decoded()
    {
        StringBuilder sb = new StringBuilder(50);

        sb.append("v").append(Version).append(", ");
        sb.append("TargetSrc: ").append(TargetSource).append(", ");
        sb.append("StreamSrc: ").append(StreamerSource).append(", ");
        sb.append("ProcessEn: ");
        for (int i = 0; i < ProcessingEnable.size(); i++)
        {
            sb.append(ProcessingEnable.get(i));
            if (i + 1 < ProcessingEnable.size())
                sb.append(",");
        }
        
        return sb.toString();
    }
}
