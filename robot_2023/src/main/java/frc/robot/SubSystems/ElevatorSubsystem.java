package frc.robot.SubSystems;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import frc.robot.Configuration;

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class ElevatorSubsystem extends SubsystemBase {
    private final CANSparkMax m_rotateMotor;
    private final TalonSRX m_extendMotor;
    private final PIDController m_pid = new PIDController(0.05, 0.0001, 0);
    private final RelativeEncoder m_rotateEnc;
    private double m_desiredArmPosition = 0;

    /** Creates a new DriveSubsystem. */
    public ElevatorSubsystem(int rotateMotorID, int extendMotorID) {
        // The SparkMax controls a Neo motor
        m_rotateMotor = new CANSparkMax(rotateMotorID, MotorType.kBrushless);
        // The Talon Controls a BAG motor
        m_extendMotor = new TalonSRX(extendMotorID);
        m_rotateMotor.restoreFactoryDefaults();
        m_rotateMotor.setSmartCurrentLimit(Configuration.NeoLimit);
        m_extendMotor.configFactoryDefault();
        m_extendMotor.configPeakCurrentLimit(Configuration.BagMotorLimit);

        /*
         * When the arm is in the "front" of the robot, negative motor
         * rotations rotate the arm towards the back and positive rotations
         * rotate the arm towards the front.
         */
        m_rotateEnc = m_rotateMotor.getEncoder();
        m_rotateEnc.setPosition(0);

        /*
         * We could do a few different things here. We could have three buttons
         * to drive the arm to the high, mid, and low positions on the grid.
         *
         * We cold also just use a joystick with a PID controller.
         * This would work by having the up/down axis of the joystick, adjust
         * the desired position to "park" the arm. Then the PID controller
         * closes the loop and tries to move the arm to that location.
         */
    }

    @Override

    public void periodic() {
        var output = m_pid.calculate(m_rotateEnc.getPosition());
        m_rotateMotor.set(output);
        SmartDashboard.putNumber("ArmEnc", m_rotateEnc.getPosition());
        SmartDashboard.putNumber("ArmMotorDrive", output);
    }

    public void control(double rotation, double extend)
    {
        //m_rotateMotor.set(rotation * 0.25);
        if (Math.abs(rotation) > Configuration.RotationDeadband)
        {
            /*
             * Now we are going to use the rotation to set the desired encoder
             * set point.
             */
            m_desiredArmPosition += (rotation * Configuration.RotationScale);
            m_pid.setSetpoint(m_desiredArmPosition);
        }
        if (Math.abs(extend) > Configuration.ExtendDeadband)
        {
            // Just run the motor, we might need a PID to control this as well
            m_extendMotor.set(TalonSRXControlMode.PercentOutput, extend * 0.10);
        }
    }

    public Command MoveArmToLow()
    {
        m_desiredArmPosition = -4;
        return new PrintCommand("Low");
    }

    public Command MoveArmToMid()
    {
        m_desiredArmPosition = -8;
        return new PrintCommand("Mid");
    }

    public Command MoveArmToHigh()
    {
        m_desiredArmPosition = -12;
        return new PrintCommand("High");
    }
}
