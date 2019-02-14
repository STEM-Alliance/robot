package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.wrist.WristNone;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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

    private boolean inCargoMode = false;

    public Wrist()
    {
        deployer = new DoubleSolenoid(0, 6, 7);
        setDeployer(false);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new WristNone());
    }

    public void cacheSensors(boolean isDisabled)
    {

    }

    public void reportState()
    {
        SmartDashboard.putBoolean("Wrist Cargo Mode", inCargoMode());
        SmartDashboard.putBoolean("Wrist Hatch Mode", !inCargoMode());
    }

    /**
     *    false = hatch mode
     *    true = cargo mode
     */
    public synchronized boolean inCargoMode()
    {
        return inCargoMode;
    }

    public synchronized void setDeployer(boolean wantCargoMode)
    {
        Value desired = (wantCargoMode) ? Value.kForward : Value.kReverse;
        deployer.set(desired);
        inCargoMode = wantCargoMode;
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));

        return report;
    }
}
