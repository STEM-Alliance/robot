package org.wfrobotics.hardware;

import org.wfrobotics.LookupTable;

import edu.wpi.first.wpilibj.AnalogInput;

public class SharpDistance extends AnalogInput {

    //TODO get lookup tables to work
    double[] voltageArray = {.6, .65, .7, .8, .9, .95, 1.45, 1.9, 3.6};
    double[] distanceArray = {150, 125, 100, 85, 70, 50, 30, 20, 10}; //In cm
    
    public LookupTable table;

    public SharpDistance(int channel)
    {
        super(channel);
        table = new LookupTable(voltageArray, distanceArray);
    }

    public double getDistance()
    {
        // get the value from the sensor
        double x = super.getVoltage();
        
        // convert the value to distance
        double distance = 58.772* Math.pow(x, -1.519);//table.get(x)/2.54;
        
        return distance;
    }
}