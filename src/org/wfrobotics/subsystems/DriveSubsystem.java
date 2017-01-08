package org.wfrobotics.subsystems;

import org.wfrobotics.Vector;

import edu.wpi.first.wpilibj.command.Subsystem;

public abstract class DriveSubsystem extends Subsystem {


    public abstract void driveTank(double right, double left);
    
    public abstract void drivePolar(double magnitude, double angle, double rotation);
    
    public abstract void driveVector(Vector velocity, double rotation);
    
    public abstract void driveCartesian(double x, double y, double rotation);

}
