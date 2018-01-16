package org.wfrobotics.reuse.subsystems.drive;

import edu.wpi.first.wpilibj.command.Subsystem;

public interface DriveService<T extends Subsystem & Drive>
{
    public T getDrive();
}
