package org.wfrobotics.reuse.subsystems.vision.messages;

import java.util.ArrayList;
import java.util.List;

public class VisionMessageTargets
{
    public int Version;
    public int Source;
    public int ImageWidth;
    public int ImageHeight;
    public double FPS;
    public double Timestamp;
    public List<VisionTargetInfo> Targets = new ArrayList<VisionTargetInfo>();
    
    public final String msg;
    

    /**
     * Empty constructor
     */
    public VisionMessageTargets()
    {
        this.msg = "";
        this.Version = 0;
        this.Source = 0;
        this.ImageWidth = 0;
        this.ImageHeight = 0;
        this.FPS = 0;
        this.Timestamp = 0;
        this.Targets = new ArrayList<VisionTargetInfo>();
    }
    
    /**
     * Parse a Targets message
     * @param msg raw message string
     */
    public VisionMessageTargets(String msg)
    {
        this.msg = msg;
        this.Targets = new ArrayList<VisionTargetInfo>();
        
        // Message is formatted as comma separated list of
        // MsgLength,Version,Source,ImageWidth,ImageHeight,FPS,Timestamp,TargetCount,{Targets}
        // Targets repeat and are formatted as
        // Index,CenterX,CenterY,Width,Height
        String[] parts = msg.split(",");
        int index = 0;
        
        @SuppressWarnings("unused")
        int messageLen = Integer.parseInt(parts[index++]);
        
        this.Version = Integer.parseInt(parts[index++]);
        
        this.Source = Integer.parseInt(parts[index++]);

        this.ImageWidth = Integer.parseInt(parts[index++]);
        
        this.ImageHeight = Integer.parseInt(parts[index++]);

        this.FPS = Double.parseDouble(parts[index++]);
        
        this.Timestamp = Double.parseDouble(parts[index++]);
        
        int count = Integer.parseInt(parts[index++]);
        
        for(int i = 0; i < count; i++)
        {
            VisionTargetInfo target = new VisionTargetInfo(Double.parseDouble(parts[index++]),
                                       Double.parseDouble(parts[index++]),
                                       Double.parseDouble(parts[index++]),
                                       Double.parseDouble(parts[index++]));
            
            this.Targets.add(target);
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
    public String Decoded()
    {
        StringBuilder sb = new StringBuilder(50);

        sb.append("v").append(Version).append(", ");
        sb.append("Src: ").append(Source).append(", ");
        sb.append("Img: ").append(ImageWidth).append("x").append(ImageHeight).append(", ");
        sb.append("FPS: ").append(FPS).append(", ");
        sb.append("Time: ").append(Timestamp).append(", ");
        sb.append("Targets: " + Targets.size());
        
        return sb.toString();
    }
}
