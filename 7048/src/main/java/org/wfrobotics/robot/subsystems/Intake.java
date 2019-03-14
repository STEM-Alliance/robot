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
    private final DoubleSolenoid holder;
    private boolean hatchOut;

    private Intake()
    {
        final RobotConfig config = RobotConfig.getInstance();

        holder = new DoubleSolenoid(0, config.kAddressSolenoidPoppersF, config.kAddressSolenoidPoppersB);
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
        hatchOut = out;
        Value desired = (out) ? Value.kForward : Value.kReverse;
        holder.set(desired);
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));

        return report;
    }

	public boolean getPoppers() {
		return hatchOut;
	}
}
