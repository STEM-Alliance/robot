package org.wfrobotics.reuse.subsystems.tank;

import org.wfrobotics.reuse.commands.differential.DriveTank;
import org.wfrobotics.reuse.subsystems.drive.DifferentialDrive;
import org.wfrobotics.reuse.utilities.HerdVector;

import edu.wpi.first.wpilibj.command.Subsystem;

public class TankSubsystem extends Subsystem implements DifferentialDrive
{
    private static TankSubsystem instance = null;

    private TankSubsystem()
    {

    }

    public static TankSubsystem getInstance()
    {
        if (instance == null) { instance = new TankSubsystem(); }
        return instance;
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new DriveTank());
    }

    public void driveBasic(HerdVector vector)
    {

    }

    public void turnBasic(HerdVector vector)
    {

    }

    public void driveDifferential(double left, double right)
    {

    }

    public void setBrake(boolean enable)
    {

    }

    public void setGear(boolean useHighGear)
    {

    }

    public void zeroGyro()
    {

    }
}
