package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.intake.IntakeNone;
import org.wfrobotics.robot.config.RobotConfig;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Intake extends EnhancedSubsystem
{
    public static Intake getInstance()
    {
        if (instance == null)
        {
            instance = new Intake();
        }
        return instance;
    }

    private static Intake instance = null;
    private final DoubleSolenoid poppers;

    private Intake()
    {
        final RobotConfig config = RobotConfig.getInstance();

        poppers = new DoubleSolenoid(0, config.kAddressSolenoidPoppersF, config.kAddressSolenoidPoppersB);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new IntakeNone());
    }

    public void cacheSensors(boolean isDisabled)
    {

    }

    public void reportState()
    {

    }

    public void setPoppers(boolean out)
    {
        Value desired = (out) ? Value.kForward : Value.kReverse;
        poppers.set(desired);
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));

        return report;
    }
}
