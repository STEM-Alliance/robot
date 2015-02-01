package com.taurus.robotspecific2015;

import edu.wpi.first.wpilibj.PWM;

public class LEDs {
    
    public class Color {
        public int Red;
        public int Green;
        public int Blue;
        
        public Color(int Red, int Green, int Blue)
        {
            this.Red = Red;
            this.Green = Green;
            this.Blue = Blue;
        }
    }

    private PWM Red;
    private PWM Green;
    private PWM Blue;
    
    public LEDs()
    {
        Red = new PWM(8);
        Green = new PWM(9);
        Blue = new PWM(7);
    }

    public void Set(Color value)
    {
        Red.setRaw(value.Red);
        Green.setRaw(value.Green);
        Blue.setRaw(value.Blue);
    }
    public void Set(int r, int g, int b)
    {
        Red.setRaw(r);
        Green.setRaw(g);
        Blue.setRaw(b);
    }
}
