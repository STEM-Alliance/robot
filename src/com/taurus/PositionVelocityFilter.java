package com.taurus;

/**
 * A filter that tracks velocity given position readings and returns interpolated position values
 * @author Team 4818 Taurus Robotics
 */
public class PositionVelocityFilter
{
    private final double maxSensorError;
    private final double extrapolateTime; 
    
    private double lastTimestamp;
    private double position;
    private double velocity;
    
    /**
     * Constructs a new filter instance with smoothing parameters
     * @param maxSensorError How far aways can the sensor reading be from the true position
     * @param extrapolateTime How far into the future should the filter extrapolate the position based on the velocity
     */
    public PositionVelocityFilter(double maxSensorError, double extrapolateTime)
    {
        this.maxSensorError = maxSensorError;
        this.extrapolateTime = extrapolateTime;
        this.lastTimestamp = 0;
        this.position = 0;
        this.velocity = 0;
    }
    
    /**
     * Get the calculated velocity
     * @return the velocity
     */
    public double getVelocity()
    {
        return velocity;
    }
    
    /**
     * Get the interpolated position
     * @return the position
     */
    public double getPosition()
    {
        return position;
    }
    
    /**
     * Update the position and velocity based on the sensor reading and the timestamp
     * @param sensorReading The position sensor reading
     * @param timestamp The time the sensor reading was made
     */
    public void updateEstimate(double sensorReading, double timestamp)
    {
        // Time delta.
        double dt = timestamp - this.lastTimestamp;
        this.lastTimestamp = timestamp;
        
        // New position estimate (ignoring the sensor reading).
        double positionEstimate = this.position + this.velocity * dt;

        // How much weight to give to the estimate over the sensor reading.
        // The estimate decays over time starting at extrapolateTime.
        double estimateWeight = Utilities.clampToRange(extrapolateTime / dt, 0, 1);
        
        // Calculate the position as a linear combination of the sensor reading and the estimate
        double newPosition = 
                sensorReading
                + Utilities.clampToRange(
                        (positionEstimate - sensorReading) * estimateWeight, 
                        -maxSensorError, maxSensorError);
        
        // Calculate the velocity based on the change in position
        this.velocity = (newPosition - this.position) / dt;
        
        // Save the new position
        this.position = newPosition;
    }
    
    
}
