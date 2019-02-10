package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Climb extends EnhancedSubsystem
{
    public static Climb getInstance()
    {
        if (instance == null)
        {
            instance = new Climb();
        }
        return instance;
    }
    private static Climb instance;

    DoubleSolenoid popper0 = new DoubleSolenoid(0, 4, 5);

    public Climb()
    {
    }

    public void setPoppers(boolean out)
    {
        Value desired = (out) ? Value.kForward : Value.kReverse;
        popper0.set(desired);
    }

    public void reportState()
    {
    }

    public TestReport runFunctionalTest()
    {
        return null;
    }

    public void cacheSensors(boolean isDisabled)
    {
    }

    protected void initDefaultCommand()
    {
    }
}
