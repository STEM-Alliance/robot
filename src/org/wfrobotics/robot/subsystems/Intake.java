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
    private final TalonSRX motorCargo, motorHatch;
    private final DoubleSolenoid poppers;
    private final DigitalInput hatchSensor;

    public Intake()
    {
        final RobotConfig config = RobotConfig.getInstance();

        motorCargo = TalonFactory.makeTalon(config.kAddressTalonCargo);
        motorHatch = TalonFactory.makeTalon(config.kAddressTalonHatch);
        poppers = new DoubleSolenoid(0, config.kAddressSolenoidPoppersF, config.kAddressSolenoidPoppersB);
        hatchSensor = new DigitalInput(config.kAddressDigitalHatchSensor);

        setPoppers(false);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new IntakeOpenLoop());
    }

    public void cacheSensors(boolean isDisabled)
    {

    }

    public void reportState()
    {
        SmartDashboard.putBoolean("HatchAtTop", hatchSensor.get());
    }

    public void setCargoSpeed(double percent)
    {
        motorCargo.set(ControlMode.PercentOutput, percent);
    }

    public void setHatchSpeed(double percent)
    {
        motorHatch.set(ControlMode.PercentOutput, percent);
    }

    public void setPoppers(boolean out)
    {
        Value desired = (out) ? Value.kForward : Value.kReverse;
        poppers.set(desired);
    }

    public boolean hasHatch()
    {
        return hatchSensor.get();
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));
        report.add(TalonChecker.checkFirmware(motorHatch));
        report.add(TalonChecker.checkFirmware(motorHatch));
        report.add(TalonChecker.checkFrameRates(motorHatch));

        return report;
    }
}
