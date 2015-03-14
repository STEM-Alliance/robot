package com.taurus.robotspecific2015;

import edu.wpi.first.wpilibj.Ultrasonic;

public class UltraSonicSensor {
    private int output = 6;
    private int input = 7;
    public Ultrasonic ultra;

    public UltraSonicSensor()
    {
        ultra = new Ultrasonic(output, input);

    }

    public double getDistance()
    {
        
        ultra.setAutomaticMode(true);
        double distance = ultra.getRangeInches();
        return distance;

    }
}
