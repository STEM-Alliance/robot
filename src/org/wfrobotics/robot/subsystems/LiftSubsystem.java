package org.wfrobotics.robot.subsystems;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.commands.Elevate;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
        BottomSensor = new DigitalInput(0);
        TopSensor = new DigitalInput(1);

        //LiftMotor.setNeutralMode(NeutralMode.Brake);
        LiftMotor.setInverted(true);

        int absolutePosition = LiftMotor.getSelectedSensorPosition(10) & 0xFFF;

        LiftMotor.setSelectedSensorPosition(absolutePosition, 0, 10);

        LiftMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        LiftMotor.setSensorPhase(true);

        LiftMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 5, 10);
        LiftMotor.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, 5, 10);
        LiftMotor.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 1 , 10);
        //LiftMotor.setControlFramePeriod(ControlFrame.Control_3_General, 1);
        LiftMotor.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 5, 10);
        LiftMotor.setStatusFramePeriod(StatusFrame.Status_4_AinTempVbat, 5, 10);

        LiftMotor.setNeutralMode(NeutralMode.Brake);

        LiftMotor.configAllowableClosedloopError(5, 0, 10);
        LiftMotor.config_kF(0, 0.0, 10);
        LiftMotor.config_kP(0, 20, 10);
        LiftMotor.config_kI(0, .5, 10);
        LiftMotor.config_kD(0, 500, 10);
        LiftMotor.configNeutralDeadband(.05, 10);
        LiftMotor.config_IntegralZone(0, 1, 10);
    }

    public void goToPosition(double destination)
    {

        update();
        LiftMotor.set(ControlMode.Position, (destination*4096));
    }

    public void setSpeed (double speed)
    {
        double output = speed;

        if(isAtBottom() && speed < 0 || isAtTop() && speed > 0)
        {
            output = 0;
        }

        update();
        LiftMotor.set(ControlMode.PercentOutput, output);
    }

    private void update()
    {
        zeroPositionIfNeeded();
        SmartDashboard.putNumber("LiftEncoder", Robot.liftSubsystem.getEncoder());
    }

    public void zeroPositionIfNeeded()
    {
        if(Robot.liftSubsystem.isAtBottom())
        {
            Robot.liftSubsystem.LiftMotor.setSelectedSensorPosition(0, 0, 0);
        }
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
