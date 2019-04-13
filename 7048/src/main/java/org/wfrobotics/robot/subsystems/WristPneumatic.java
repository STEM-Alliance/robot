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
import org.wfrobotics.robot.commands.wrist.WristOpenLoop;
import org.wfrobotics.robot.config.RobotConfig;

public class WristPneumatic extends EnhancedSubsystem
{
    public static WristPneumatic getInstance()
    {
        if(instance == null)
        {
            instance = new WristPneumatic();
        }
        return instance;
    }

    private static WristPneumatic instance = null;
    private final DoubleSolenoid wrist;
    private boolean wristUp;

    private WristPneumatic()
    {
        final RobotConfig config = RobotConfig.getInstance();
        wrist = new DoubleSolenoid(0, config.kAddressSolenoidWristB, config.kAddressSolenoidWristF);
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
        wristUp = up;
        Value desired = (up) ? Value.kForward : Value.kReverse;
        wrist.set(desired);
    }

    public boolean getWrist()
    {
        return wristUp;
    }

    public void cacheSensors(boolean a)
    {

    }

    public TestReport runFunctionalTest()
    {
        return new TestReport();
    }

}
