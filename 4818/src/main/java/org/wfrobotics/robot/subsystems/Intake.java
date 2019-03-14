package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.LimitSwitch;
import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.intake.IntakeOpenLoop;
import org.wfrobotics.robot.commands.intake.SmartIntake;
import org.wfrobotics.robot.config.PnuaticConfig;
import org.wfrobotics.robot.config.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    private final TalonSRX motor;
    private final DoubleSolenoid grabber;
    
    protected CachedIO cachedIO = new CachedIO();

    public Intake()
    {
        final RobotConfig config = RobotConfig.getInstance();
        final PnuaticConfig pConfig = RobotConfig.getInstance().getPnumaticConfig();
        motor = TalonFactory.makeTalon(config.kAddressTalonCargo);
        motor.setInverted(config.kInvertTalonCargo);
        grabber = new DoubleSolenoid(pConfig.kAddressPCMPoppers, pConfig.kAddressSolenoidPoppersF, pConfig.kAddressSolenoidPoppersB);

        setGrabber(false);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new SmartIntake());
    }

    public void cacheSensors(boolean isDisabled)
    {

    }

    public void reportState()
    {
        SmartDashboard.putString("Intake Command", getCurrentCommandName());
    }

    public void setCargoSpeed(double percent)
    {
        SmartDashboard.putNumber("intake Speed", percent);
        motor.set(ControlMode.PercentOutput, percent);
    }
    public void setGrabber(boolean out)
    {
        Value desired = (out) ? Value.kForward : Value.kReverse;
        grabber.set(desired);
        SuperStructure.getInstance().setStoreHatch(out);
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));
        report.add(TalonChecker.checkFirmware(motor));
        report.add(TalonChecker.checkFrameRates(motor));

        return report;
    }
    
    protected static class CachedIO
    {

    }
    
}
