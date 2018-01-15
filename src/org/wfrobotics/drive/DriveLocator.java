package org.wfrobotics.drive;

import edu.wpi.first.wpilibj.command.Subsystem;

public interface DriveLocator<T extends Subsystem & Drive>
{
    public T getDrive();
}
