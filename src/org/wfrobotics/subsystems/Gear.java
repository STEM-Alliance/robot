package org.wfrobotics.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Gear extends Subsystem {

    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
    }

    /**
     * Tell if the subsystem senses it possesses a stored gear
     * @return Do we have a stored gear?
     */
    public boolean isGearStored()
    {
        DriverStation.reportError("Gear is gear stored not implemented yet", true);
        
        return false;  // TODO DRL return sensor feedback
    }
    
    /**
     * Tell if the subsystem senses the airship spring is piercing the stored gear
     * @return Is the spring piercing the stored gear?
     */
    public boolean isSpringInGear()
    {
        DriverStation.reportError("Gear is spring in gear not implemented yet", true);
        
        return false;  // TODO DRL return sensor feedback
    }
}
