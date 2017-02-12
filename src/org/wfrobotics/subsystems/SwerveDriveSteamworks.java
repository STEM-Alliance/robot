package org.wfrobotics.subsystems;

import org.wfrobotics.commands.SteamworksDrive;
import org.wfrobotics.subsystems.drive.swerve.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj.DriverStation;

public class SwerveDriveSteamworks extends SwerveDriveSubsystem 
{
    public SwerveDriveSteamworks()
    {
        // TODO setup some digital inputs to read the sensors
    }

    @Override
    public void initDefaultCommand() 
    {
        setDefaultCommand(new SteamworksDrive());
    }

    /**
     * Tell if the subsystem senses it possesses a stored gear
     * @return Do we have a stored gear?
     */
    public boolean isGearStored()
    {
        DriverStation.reportError("SteamworksDrive is gear stored not implemented yet", true);
        
        return false;  // TODO DRL return sensor feedback
    }
    
    /**
     * Tell if the subsystem senses the airship spring is piercing the stored gear
     * @return Is the spring piercing the stored gear?
     */
    public boolean isSpringInGear()
    {
        DriverStation.reportError("SteamworksDrive is spring in gear not implemented yet", true);
        
        return false;  // TODO DRL return sensor feedback
    }

}
