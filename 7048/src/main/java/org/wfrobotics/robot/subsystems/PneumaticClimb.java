package org.wfrobotics.robot.subsystems;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

import org.wfrobotics.robot.commands.wrist.WristNone;
import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem;
import org.wfrobotics.robot.config.RobotConfig;

public class PneumaticClimb extends EnhancedSubsystem
{
    public static PneumaticClimb getInstance()
    {
        if(instance == null)
        {
            instance = new PneumaticClimb();
        }
        return instance;
    }

    private static PneumaticClimb instance = null;
    private DoubleSolenoid climbPiston1 = null;
    private DoubleSolenoid climbPiston2 = null;
    private boolean climbUp = false;

    private PneumaticClimb()
    {
        final RobotConfig config = RobotConfig.getInstance();
        climbPiston1 = new DoubleSolenoid(0, config.kAddressClimb1B, config.kAddressClimb1F);
        climbPiston2 = new DoubleSolenoid(0, config.kAddressClimb2B, config.kAddressClimb2F);
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new WristNone());
    }

    public void reportState()
    {

    }

    public void setWrist(boolean up)
    {
        climbUp = up;
        Value desired = (up) ? Value.kForward : Value.kReverse;
        climbPiston1.set(desired);
        climbPiston2.set(desired);
    }

    public boolean getClimb()
    {
        return climbUp;
    }

    public void cacheSensors(boolean a)
    {

    }

    public TestReport runFunctionalTest()
    {
        return new TestReport();
    }

}
