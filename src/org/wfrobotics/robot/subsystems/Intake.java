package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.intake.hatch.Stop;

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

    private static Intake instance;

    // TODO: move the tallon id and lmtsw to the robot config
    TalonSRX intakeMtr = new TalonSRX(10);
    DigitalInput topLmt = new DigitalInput(0);

    DoubleSolenoid popper0 = new DoubleSolenoid(0, 0, 1);
    DoubleSolenoid popper1 = new DoubleSolenoid(0, 2, 3);

    public Intake()
    {

    }

    protected void initDefaultCommand(){ setDefaultCommand(new Stop());}

    public void cacheSensors(boolean isDisabled)
    {

    }

    public void reportState()
    {
        SmartDashboard.putBoolean("HatchAtTop", topLmt.get());
    }

    public void setPoppers(boolean out)
    {
        Value desired = (out) ? Value.kForward : Value.kReverse;
        popper0.set(desired);
        popper1.set(desired);
    }

    public void setSpeed(double speed)
    {
        intakeMtr.set(ControlMode.PercentOutput, speed);
    }

    public boolean isHatchAtTop()
    {
        return topLmt.get();
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));
        report.add(TalonChecker.checkFirmware(intakeMtr));
        report.add(TalonChecker.checkFirmware(intakeMtr));
        report.add(TalonChecker.checkFrameRates(intakeMtr));
        report.add(!topLmt.get());

        return report;
    }
}
