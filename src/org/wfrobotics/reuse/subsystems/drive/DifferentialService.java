package org.wfrobotics.reuse.subsystems.drive;

import edu.wpi.first.wpilibj.command.Subsystem;

public interface DifferentialService<T extends Subsystem & DifferentialDrive> extends DriveService<T>
{
    public T getDrive();
}
