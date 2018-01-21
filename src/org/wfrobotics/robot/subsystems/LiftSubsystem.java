package org.wfrobotics.robot.subsystems;

import org.wfrobotics.robot.commands.Elevate;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class LiftSubsystem extends Subsystem
{
    public TalonSRX LiftMotor;
    public DigitalInput BottomSensor;
    public DigitalInput TopSensor;
    public int encoderValue;

    public LiftSubsystem()
    {
        LiftMotor = new TalonSRX(18);
        BottomSensor = new DigitalInput(1);
        TopSensor = new DigitalInput(0);
        LiftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
    }

    public boolean isAtTop()
    {
        return TopSensor.get();
    }

    public boolean isAtBottom()
    {
        return BottomSensor.get();
    }

    public int getEncoder()
    {
        return LiftMotor.getSelectedSensorPosition(0);
    }

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public void initDefaultCommand()
    {
        // Set the default command for a subsystem here.
        setDefaultCommand(new Elevate(0));
    }
}
