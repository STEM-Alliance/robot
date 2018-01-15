package org.wfrobotics.drive;

import edu.wpi.first.wpilibj.command.Subsystem;

//public abstract class HolonomicLocator<T extends Subsystem & HolonomicDrive> implements DriveLocator<T>
//{
//    private T drive;
//
//    public HolonomicLocator(T holonomicDriveSubsystem)
//    {
//        drive = holonomicDriveSubsystem;
//    }
//
//    public T getDrive()
//    {
//        return drive;
//    }
//}

public interface HolonomicLocator<T extends Subsystem & HolonomicDrive> extends DriveLocator<T>
{
    public T getDrive();
}