package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Wrist extends EnhancedSubsystem
{
    public static Wrist getInstance()
    {
        if (instance == null)
        {
            instance = new Wrist();
        }
        return instance;
    }

    private static Wrist instance = null;
    private final DoubleSolenoid deployer;


    /**
     *    false = hatch mode
     *    true = cargo mode
     */
    public boolean state = false;
    public void setState(boolean state)
    {
        this.state = state;
    }

    public Wrist()
    {
        deployer = new DoubleSolenoid(0, 6, 7);

    }

    public void setPoppers(boolean out)
    {
        Value desired = (out) ? Value.kForward : Value.kReverse;
        deployer.set(desired);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new WristOpenLoop());
    }

    @Override
    public void reportState()
    {

    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));

        return report;
    }

    public void cacheSensors(boolean isDisabled)
    {
    }
}
