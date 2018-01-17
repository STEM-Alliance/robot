package org.wfrobotics.reuse.subsystems.vision.messages;

import java.util.ArrayList;
import java.util.List;

public class VisionMessageTargets
{
    public int version;
    public int source;
    public int imageWidth;
    public int imageHeight;
    public double fps;
    public double timestamp;
    public List<VisionTargetInfo> Targets = new ArrayList<VisionTargetInfo>();

    public final String msg;

    public VisionMessageTargets()
    {
        msg = "";
        version = 0;
        source = 0;
        imageWidth = 0;
        imageHeight = 0;
        fps = 0;
        timestamp = 0;
        Targets = new ArrayList<VisionTargetInfo>();
    }

    /**
     * Parse a Target's message
     * @param msg raw message string
     */
    public VisionMessageTargets(String msg)
    {
        this.msg = msg;
        Targets = new ArrayList<VisionTargetInfo>();

        // Message is formatted as comma separated list of
        // MsgLength,Version,Source,ImageWidth,ImageHeight,FPS,Timestamp,TargetCount,{Targets}
        // Targets repeat and are formatted as
        // Index,CenterX,CenterY,Width,Height
        String[] parts = msg.split(",");
        int index = 0;

        Integer.parseInt(parts[index++]);
        version = Integer.parseInt(parts[index++]);
        source = Integer.parseInt(parts[index++]);
        imageWidth = Integer.parseInt(parts[index++]);
        imageHeight = Integer.parseInt(parts[index++]);
        fps = Double.parseDouble(parts[index++]);
        timestamp = Double.parseDouble(parts[index++]);

        int count = Integer.parseInt(parts[index++]);

        for(int i = 0; i < count; i++)
        {
            Targets.add(new VisionTargetInfo(Double.parseDouble(parts[index++]), Double.parseDouble(parts[index++]), Double.parseDouble(parts[index++]), Double.parseDouble(parts[index++])));
        }

    }

    /**
     * Get the raw message string
     * @return
     */
    @Override
    public String toString()
    {
        return msg;
    }

    /**
     * Get a decoded/human-readable string
     * @return
     */
    public String decoded()
    {
        StringBuilder sb = new StringBuilder(50);

        sb.append("v").append(version).append(", ");
        sb.append("Src: ").append(source).append(", ");
        sb.append("Img: ").append(imageWidth).append("x").append(imageHeight).append(", ");
        sb.append("FPS: ").append(fps).append(", ");
        sb.append("Time: ").append(timestamp).append(", ");
        sb.append("Targets: " + Targets.size());

        return sb.toString();
    }
}
