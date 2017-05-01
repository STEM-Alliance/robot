package org.wfrobotics.reuse.subsystems.motor2.examples;

import org.wfrobotics.reuse.subsystems.motor2.ClosedLoopMotor;
import org.wfrobotics.reuse.utilities.HerdLogger;

import edu.wpi.first.wpilibj.command.Subsystem;

public class RotationSubsystem extends Subsystem
{
    public enum POSITION 
    { 
        TOP(180),
        BOTTOM(0),
        TRANSPORT(45);
        
        public double location;
        
        POSITION(int location)
        {
            this.location = location;
        }
    }
    
    private final double TOLERANCE;
    
    private final HerdLogger log = new HerdLogger(RotationSubsystem.class);
    private final ClosedLoopMotor smartMotor;
    
    public RotationSubsystem(ClosedLoopMotor closedLoop, double tolerance)
    {
        smartMotor = closedLoop;
        TOLERANCE = tolerance;
    }
    
    public String toString()
    {
        return String.format("(%.2f\u00b0)", smartMotor.get());
    }

    protected void initDefaultCommand()
    {
        // TODO Set a command to always run when no other command is kicked off by the students or autonomous
    }
    
    public void set(POSITION desired)
    {
        smartMotor.set(desired.location);
        smartMotor.update(desired.location);
        
        log.debug("Arm", this);
    }
    
    public boolean atPosition(POSITION p)
    {
        double actual = smartMotor.get();
        
        // TODO Look at any limit switches or other criteria
        
        return Math.abs(p.location - actual) < TOLERANCE;
    }
}
