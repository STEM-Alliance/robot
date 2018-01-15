package org.wfrobotics.drive;

import edu.wpi.first.wpilibj.command.Subsystem;

public class TankSubsystem extends Subsystem implements DifferentialDrive
{
    private static TankSubsystem instance = null;

    private TankSubsystem()
    {

    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new DriveTank(TankLocator.getInstance()));
    }

    public static TankSubsystem getInstance()
    {
        if (instance == null)
        {
            instance = new TankSubsystem();
        }
        return instance;
    }

    public void drive(double left, double right)
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
