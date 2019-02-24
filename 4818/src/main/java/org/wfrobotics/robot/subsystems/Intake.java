package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.intake.IntakeOpenLoop;
import org.wfrobotics.robot.config.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
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
    private final TalonSRX motorCargo;
    private final DoubleSolenoid grabber;
    private final DigitalInput hatchSensor;
    
    protected CachedIO cachedIO = new CachedIO();

    public Intake()
    {
        final RobotConfig config = RobotConfig.getInstance();

        motorCargo = TalonFactory.makeTalon(config.kAddressTalonCargo);
        grabber = new DoubleSolenoid(config.kAddressPCMPoppers, config.kAddressSolenoidPoppersF, config.kAddressSolenoidPoppersB);
        hatchSensor = new DigitalInput(config.kAddressDigitalHatchSensor);

        setGrabber(false);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new IntakeOpenLoop());
    }

    public void cacheSensors(boolean isDisabled)
    {
        cachedIO.hasHatch = hatchSensor.get();
    }

    public void reportState()
    {
        SmartDashboard.putBoolean("Has Hatch", hasHatch());
    }

    public void setCargoSpeed(double percent)
    {
        motorCargo.set(ControlMode.PercentOutput, percent);
    }

    public void setGrabber(boolean out)
    {
        Value desired = (out) ? Value.kForward : Value.kReverse;
        grabber.set(desired);
    }

    public boolean hasHatch()
    {
        return cachedIO.hasHatch;
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));
        report.add(TalonChecker.checkFirmware(motorCargo));
        report.add(TalonChecker.checkFrameRates(motorCargo));

        return report;
    }
    
    protected static class CachedIO
    {
        public boolean hasHatch;
    }
}
