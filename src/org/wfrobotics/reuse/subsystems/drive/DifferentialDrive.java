package org.wfrobotics.reuse.subsystems.drive;

public interface DifferentialDrive extends Drive
{
    public void driveDifferential(double left, double right);
}
