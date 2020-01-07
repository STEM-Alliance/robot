package frc.robot.reuse.hardware.sensors;

import edu.wpi.first.wpilibj.AnalogInput;

public class SharpDistance extends AnalogInput
{
    double[] voltageArray = {.6, .65, .7, .8, .9, .95, 1.45, 1.9, 3.6};
    double[] distanceArray = {150, 125, 100, 85, 70, 50, 30, 20, 10}; // Centimeters

    public SharpDistance(int channel)
    {
        super(channel);
    }

    public double getDistanceInches()
    {
        double raw = super.getVoltage();
        return 58.772 * Math.pow(raw, -1.519) / 2.54;
    }
}