package org.wfrobotics.reuse.subsystems.drive;

import edu.wpi.first.wpilibj.command.Subsystem;


public interface HolonomicService<T extends Subsystem & HolonomicDrive> extends DriveService<T>
{
    public T getDrive();
}