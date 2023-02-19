package frc.robot.SubSystems;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.PrintCommand;

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class ElevatorSubsystem extends SubsystemBase {
    private final CANSparkMax m_rotateMotor;
    private final TalonSRX m_extendMotor;
    private final PIDController m_pid = new PIDController(0.05, 0.0001, 0);
    private final RelativeEncoder m_rotateEnc;

    /** Creates a new DriveSubsystem. */
    public ElevatorSubsystem(int rotateMotorID, int extendMotorID) {
        m_rotateMotor = new CANSparkMax(rotateMotorID, MotorType.kBrushless);
        m_extendMotor = new TalonSRX(extendMotorID);
        m_rotateMotor.restoreFactoryDefaults();
        m_rotateMotor.setSmartCurrentLimit(80);
        m_extendMotor.configPeakCurrentLimit(40);
        m_extendMotor.configFactoryDefault();

        /*
         * TODO: Need to set the motor direction and speed.
         * Do we need to setup the limit switches?
         */
        m_rotateEnc = m_rotateMotor.getEncoder();
        m_rotateEnc.setPosition(0);
        m_pid.setSetpoint(-8);
    }

    @Override

    public void periodic() {
        var error = m_pid.calculate(m_rotateEnc.getPosition());
        m_rotateMotor.set(error);
        SmartDashboard.putNumber("ArmEnc", m_rotateEnc.getPosition());
        SmartDashboard.putNumber("Error", error);
    }

    public void control(double rotation, double extend)
    {
        //m_rotateMotor.set(rotation * 0.25);
        //m_extendMotor.set(TalonSRXControlMode.PercentOutput, extend * 0.10);
    }

    // public Command mid()
    // {
    //     return PrintCommand("Mid");
    // }

    // public Command high()
    // {
    //     return PrintCommand("High");
    // }

    // public Command up()
    // {
    //     return PrintCommand("Up");
    // }
}
