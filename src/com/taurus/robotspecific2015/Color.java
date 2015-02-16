package com.taurus.robotspecific2015;


public class Color {
    public static final Color Black = new Color(0, 0, 0);
    public static final Color Red = new Color(0xfff, 0, 0);
    public static final Color Yellow = new Color(0xfff, 0x8ff, 0);
    public static final Color Green = new Color(0, 0xfff, 0);
    public static final Color Cyan = new Color(0, 0xfff, 0xfff);
    public static final Color Blue = new Color(0, 0, 0xfff);
    public static final Color Magenta = new Color(0xfff, 0, 0xfff);
    public static final Color White = new Color(0xfff, 0xfff, 0xfff);
    
    private int red;
    private int green;
    private int blue;
    
    
    public Color(int red, int green, int blue)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public void Get(boolean offset, byte[] out, int i)
    {
        if(offset)
        {
            out[i+0] |= (byte) (0x0f & green>>8); // upper 1/3
            out[i+1] = (byte) (0xff & green); // lower 2/3
            out[i+2] = (byte) (0xff & (blue>>4)); // upper 2/3
            out[i+3] = (byte) ((byte) (0xf0 & blue << 4) | (byte) (0x0f & red>>8)); //  lower 1/3, upper 1/3
            out[i+4] = (byte) (0xff & red); // lower 2/3
        }
        else
        {
            out[i+0] = (byte) (0xff & green>>4); // upper 2/3
            out[i+1] = (byte) ((byte) (0xf0 & (green << 4)) | (byte) (0x0f & (blue>>8))); // lower 1/3, upper 1/3
            out[i+2] = (byte) (0xff & blue); // lower 2/3
            out[i+3] = (byte) (0xff & (red >> 4)); // upper 2/3
            out[i+4] |= (byte) (0xf0 & (red << 4)); // lower 1/3
        }
        // green
//        out[0] = (byte) 0x0f;
//        out[1] = (byte) 0xff;
//        out[2] = (byte) 0x00;
//        out[3] = (byte) 0x00;
//        out[4] = (byte) 0x00;

        // blue
//        out[0] = (byte) 0x00;
//        out[1] = (byte) 0x00;
//        out[2] = (byte) 0xff;
//        out[3] = (byte) 0xf0;
//        out[4] = (byte) 0x00;
        
        // red
//        out[0] = (byte) 0x00;
//        out[1] = (byte) 0x00;
//        out[2] = (byte) 0x00;
//        out[3] = (byte) 0x0f;
//        out[4] = (byte) 0xff;
    }
}
