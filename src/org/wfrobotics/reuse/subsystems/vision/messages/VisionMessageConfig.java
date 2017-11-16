package org.wfrobotics.reuse.subsystems.vision.messages;

public class VisionMessageConfig extends VisionMessage
{
    private final int Version = 1;
    
    public int Source = 2;
    public int Width = 640;
    public int Height = 480;
    public int Exposure = -10;
    public int Brightness = 55;
    public int Saturation = 200;
    
    /**
     * Create a new Config Message
     * @param Source
     */
    public VisionMessageConfig(int Source)
    {
        this.Source = Source;
    }
    
    public VisionMessageConfig(int Source, int Width, int Height,
            int Exposure, int Brightness, int Saturation)
    {
        this.Source = Source;
        this.Width = Width;
        this.Height = Height;
        this.Exposure = Exposure;
        this.Brightness = Brightness;
        this.Saturation = Saturation;
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
        sb.append(Source).append(",");
        sb.append(Width).append(",");
        sb.append(Height).append(",");
        sb.append(Exposure).append(",");
        sb.append(Brightness).append(",");
        sb.append(Saturation);
        
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
        sb.append("Src: ").append(Source).append(", ");
        sb.append("Img: ").append(Width).append("x").append(Height).append(", ");
        sb.append("Exp: ").append(Exposure).append(", ");
        sb.append("Bri: ").append(Brightness).append(", ");
        sb.append("Sat: ").append(Saturation);
        
        return sb.toString();
    }
}
