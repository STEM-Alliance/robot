package org.wfrobotics;

/**
 * A filter that tracks velocity given position readings
 * 
 * @author Team 4818 Taurus Robotics
 */
public class VelocityCalculator {
    private double lastTimestamp;
    private double lastPosition;
    private double velocity;

    /**
     * Constructs a new filter instance
     */
    public VelocityCalculator()
    {
        this.lastTimestamp = 0;
        this.lastPosition = 0;
        this.velocity = 0;
    }

    /**
     * Get the calculated velocity
     * 
     * @return the velocity
     */
    public double getVelocity()
    {
        return velocity;
    }

    /**
     * Update the position and velocity based on the sensor reading and the
     * timestamp
     * 
     * @param sensorReading
     *            The position sensor reading
     * @param timestamp
     *            The time the sensor reading was made
     */
    public void updateEstimate(double sensorReading, double timestamp)
    {
        // Calculate the time delta.
        double dt = timestamp - this.lastTimestamp;
        this.lastTimestamp = timestamp;

        // Calculate the velocity based on the change in position.
        this.velocity = (sensorReading - this.lastPosition) / dt;

        // Save the new position.
        this.lastPosition = sensorReading;
    }

}
