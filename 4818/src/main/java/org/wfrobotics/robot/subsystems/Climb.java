package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.climb.ClimbNone;
import org.wfrobotics.robot.config.RobotConfig;

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

    private static Climb instance = null;
    private final DoubleSolenoid grippers;

    public Climb()
    {
        final RobotConfig config = RobotConfig.getInstance();

        grippers = new DoubleSolenoid(0, config.kAddressSolenoidGrippersF, config.kAddressSolenoidGrippersB);
        setGrippers(false);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new ClimbNone());
    }

    public void cacheSensors(boolean isDisabled)
    {

    }

    public void reportState()
    {

    }

    public void setGrippers(boolean out)
    {
        final Value desired = (out) ? Value.kForward : Value.kReverse;
        grippers.set(desired);
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        return report;
    }
}
