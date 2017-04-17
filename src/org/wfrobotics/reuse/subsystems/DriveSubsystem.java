package org.wfrobotics.reuse.subsystems;

import org.wfrobotics.Vector;
import org.wfrobotics.reuse.hardware.Gyro;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class DriveSubsystem extends Subsystem
{
    protected boolean m_fieldRelative = true;
    protected boolean m_brake = false;
    protected Gyro m_gyro;
    protected double m_lastHeading;

    public DriveSubsystem(boolean FieldRelative)
    {
        super();
        
        try 
        {
            m_gyro = Gyro.getInstance();
        } 
        catch (RuntimeException ex ) 
        {
            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        }
        finally
        {
            zeroGyro();
        }
    }
    
    public void setFieldRelative(boolean enable) { m_fieldRelative = enable; }
    public boolean getFieldRelative() { return m_fieldRelative; }

    public void zeroGyro(float offsetYaw) { m_gyro.setZero(offsetYaw); m_lastHeading = offsetYaw; }
    public void zeroGyro() { m_gyro.zeroYaw(); m_lastHeading = 0; }
    
    public void setBrake(boolean enable) { m_brake = enable; }
    public boolean getBrake() { return m_brake; }
    
    public abstract void driveTank(double right, double left);
    public abstract void driveVector(Vector velocity, double rotation);
    
    public void printDash()
    {
        SmartDashboard.putNumber("Gyro", m_gyro.getYaw());
        SmartDashboard.putNumber("Last Heading", m_lastHeading);
        SmartDashboard.putBoolean("FieldRelative", m_fieldRelative);
        SmartDashboard.putBoolean("Brake", m_brake);
    }
}