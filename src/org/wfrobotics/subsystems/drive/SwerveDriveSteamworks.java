package org.wfrobotics.subsystems.drive;

import org.wfrobotics.commands.SteamworksDrive;
import org.wfrobotics.subsystems.drive.swerve.SwerveDriveSubsystem;

public class SwerveDriveSteamworks extends SwerveDriveSubsystem {

    public SwerveDriveSteamworks()
    {
        
    }

    @Override
    public void initDefaultCommand() 
    {
        setDefaultCommand(new SteamworksDrive());
    }
}
