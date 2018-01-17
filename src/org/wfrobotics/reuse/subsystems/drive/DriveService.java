package org.wfrobotics.reuse.subsystems.drive;

import edu.wpi.first.wpilibj.command.Subsystem;

public interface DriveService extends DifferentialDrive, HolonomicDrive
{
    public Subsystem getSubsystem();
}