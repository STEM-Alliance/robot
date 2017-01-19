package org.wfrobotics.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ShooterSubsystem extends Subsystem {

    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
    }

    /**
     * Control speed of the shooting wheel(s)
     * @param speed -1 (full backward) to 1 (full forward)
     */
    public void setSpeed(double speed)
    {
        DriverStation.reportError("Shooter set speed not implemented yet", true);
    }
    
    /**
     * Tells if the current speed is at the previously set speed within this tolerance
     * @param tolerance percent above or below that counts as being at that speed (.1 = +/-10%) 
     * @return if the shooting wheel(s) is at that speed
     */
    public boolean speedReached(double tolerance)
    {
        DriverStation.reportError("Shooter speed reached not implemented yet", true);
        
        // TODO DRL get actual value based on sensor or otherwise, compare to setpoint within specified tolerance
        
        return false;  // TODO DRL
    }
}
