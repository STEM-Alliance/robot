package org.wfrobotics.reuse.subsystems.drive;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;

public interface HolonomicDrive extends Drive
{
    public void driveWithHeading(SwerveSignal command);
}
