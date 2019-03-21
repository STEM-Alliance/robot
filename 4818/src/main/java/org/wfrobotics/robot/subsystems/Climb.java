package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.climb.ClimbNone;
// import org.wfrobotics.robot.config.PnuaticConfig;
// import org.wfrobotics.robot.config.RobotConfig;

// import edu.wpi.first.wpilibj.DoubleSolenoid;
// import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public final class Climb extends EnhancedSubsystem
{
    static class SingletonHolder
    {
        static Climb instance = new Climb();
    }

    public static Climb getInstance()
    {
        return SingletonHolder.instance;
    }

    // private final DoubleSolenoid grippers;
    // private final DoubleSolenoid deployer;
    // private final DoubleSolenoid lockers;
    // private final DoubleSolenoid pushUP;

    public Climb()
    {
        // final RobotConfig config = RobotConfig.getInstance();
        // final PnuaticConfig Pconfig = config.getPnumaticConfig();

        // grippers = new DoubleSolenoid(Pconfig.kAddressPCMGrippers, Pconfig.kAddressSolenoidGrippersF, Pconfig.kAddressSolenoidGrippersB);
        // lockers = new DoubleSolenoid(Pconfig.kAddressPCMLockers, Pconfig.kAddressSolenoidLockersF, Pconfig.kAddressSolenoidLockersB);
        // pushUP = new DoubleSolenoid(Pconfig.kAddressPCMPushUp, Pconfig.KAddressSolenoidPushUpF, Pconfig.KAddressSolenoidPushUpB);
        // deployer = new DoubleSolenoid(Pconfig.kAddressPCMDeployer, Pconfig.KAddressSolenoidDeployerF, Pconfig.KAddressSolenoidDeployerB);

        setGrippers(true);
        setLockers(true);
        setPushUp(false);
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
        // final Value desired = (out) ? Value.kForward : Value.kReverse;
        // grippers.set(desired);
    }

    public void setPushUp(boolean out)
    {
        // final Value desired = (out) ? Value.kForward : Value.kReverse;
        // pushUP.set(desired);
    }

    public void setLockers(boolean out)
    {
        // final Value desired = (out) ? Value.kForward : Value.kReverse;
        // lockers.set(desired);
    }
    
    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));

        return report;
    }
}
