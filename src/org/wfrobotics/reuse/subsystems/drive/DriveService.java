package org.wfrobotics.reuse.subsystems.drive;

import edu.wpi.first.wpilibj.command.Subsystem;

public interface DriveService<T extends Subsystem & Drive> extends DifferentialDrive, HolonomicDrive
{
    public T getSubsystem();
}