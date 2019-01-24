package org.wfrobotics.robot.subsystems;

import org.wfrobotics.robot.commands.intake.stop;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;

public class IntakeSubsystem extends Subsystem
{
    TalonSRX motor = new TalonSRX(10);
    DigitalInput top;

    static IntakeSubsystem instance;

    public IntakeSubsystem()
    {
        top = new DigitalInput(0);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new stop());

    }

    public void setSpeed(double speed) {
        motor.set(ControlMode.PercentOutput, speed);
    }

    public static IntakeSubsystem getInstance()
    {
        if (instance == null)
        {
            instance = new IntakeSubsystem();
        }
        return instance;
    }

    public boolean isAtTop()
    {
        return top.get();
    }
}
