package org.wfrobotics.subsystems;

import org.wfrobotics.Vector;
import org.wfrobotics.hardware.Gyro;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;

public abstract class DriveSubsystem extends Subsystem {

    protected boolean m_fieldRelative = false;
    protected boolean m_brake = false;
    protected Gyro m_gyro;
    protected double m_lastHeading;

    public DriveSubsystem()
    {
        super();
        
        // Setup gyro
        try {
            m_gyro = Gyro.getInstance();
        } catch (RuntimeException ex ) {
            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        }
        
        gyroZero();
    }
    
    /**
     * Set whether to drive relative to the field using the gyro
     * @param val
     */
    public void setFieldRelative(boolean val) { m_fieldRelative = val; }
    
    public boolean getFieldRelative() { return m_fieldRelative; }

    
    /**
     * Zero the yaw of the {@link Gyro}
     */
    public void gyroZero() { m_gyro.zeroYaw(); m_lastHeading = 0; }

    /**
     * Set the {@link Gyro} to use a new zero value
     * 
     * @param yaw angle to offset by
     */
    public void setGyroZero(float yaw) { m_gyro.setZero(yaw); m_lastHeading = yaw; }
    
    /**
     * Set the chassis's brake
     * 
     * @param m_brake if true, set the brake, else release brake
     */
    public void setBrake(boolean val) { m_brake = val; }
    
    /**
     * Get the chassis's brake
     * 
     * @return true if brake is set, else false
     */
    public boolean getBrake() { return m_brake; }
    
    /**
     * Drive as if a tank, with individual left and right speeds. 
     * Not terribly useful unless using a tank drive
     * @param right -1..1
     * @param left -1..1
     */
    public abstract void driveTank(double right, double left);

    /**
     * Drive using magnitude and angle (velocity) and angular rotation  (Polar Plane)
     * @param magnitude -1..1
     * @param angle -180..180 (will wrap around) 
     * @param rotation -1..1
     */
    public abstract void driveVector(double magnitude, double angle, double rotation);
    
    /**
     * Drive using {@link Vector} (velocity) and angular rotation  (Polar Plane)
     * @param velocity
     * @param rotation -1..1
     */
    public abstract void driveVector(Vector velocity, double rotation);
    
    /**
     * Drive using X and Y coordinates and angular rotation (Cartesian Plane)
     * @param x -1..1
     * @param y -1..1
     * @param rotation -1..1
     */
    public abstract void driveXY(double x, double y, double rotation);

}
