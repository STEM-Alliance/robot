package org.wfrobotics.drive;

import edu.wpi.first.wpilibj.command.Subsystem;

public interface DifferentialLocator<T extends Subsystem & DifferentialDrive> extends DriveLocator<T>
{
    public T getDrive();
}
