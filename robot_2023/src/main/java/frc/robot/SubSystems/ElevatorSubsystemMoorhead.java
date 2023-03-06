package frc.robot.SubSystems;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.Configuration;

import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class ElevatorSubsystemMoorhead extends SubsystemBase {
    private final CANSparkMax m_rotateMotor;
    private final CANSparkMax m_extendMotor;
    private final PIDController m_elevatorPID = new PIDController(Configuration.ElevatorKp, Configuration.ElevatorKi, Configuration.ElevatorKd);
    private final PIDController m_extenderPID = new PIDController(Configuration.ExtenderKp, Configuration.ExtenderKi, Configuration.ExtenderKd);
    private final RelativeEncoder m_rotateEnc;
    private final RelativeEncoder m_extendEnc;
    private double m_desiredArmPosition = 0;
    private double m_desiredElevatorPosition = 0;

    /** Creates a new DriveSubsystem. */
    public ElevatorSubsystemMoorhead(int rotateMotorID, int extendMotorID) {
        // The SparkMax controls a Neo motor
        m_rotateMotor = new CANSparkMax(rotateMotorID, MotorType.kBrushless);
        // The Talon Controls a BAG motor
        m_extendMotor = new CANSparkMax(extendMotorID, MotorType.kBrushless);
        m_rotateMotor.restoreFactoryDefaults();
        m_extendMotor.restoreFactoryDefaults();
        m_rotateMotor.setSmartCurrentLimit(Configuration.NeoLimit);
        m_extendMotor.setSmartCurrentLimit(Configuration.NeoLimit);

        /*
         * When the arm is in the "front" of the robot, negative motor
         * rotations rotate the arm towards the back and positive rotations
         * rotate the arm towards the front.
         */
        m_rotateEnc = m_rotateMotor.getEncoder();
        m_rotateEnc.setPosition(0);
        m_extendEnc = m_extendMotor.getEncoder();
        m_extendEnc.setPosition(0);

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
        var elevatorOutput = m_elevatorPID.calculate(m_rotateEnc.getPosition());
        m_rotateMotor.set(elevatorOutput);
        SmartDashboard.putNumber("ArmEnc", m_rotateEnc.getPosition());
        SmartDashboard.putNumber("ArmMotorDrive", elevatorOutput);
        SmartDashboard.putNumber("ArmSetPoint", m_desiredArmPosition);

        var extenderOutput = m_extenderPID.calculate(m_extendEnc.getPosition());
        m_extendMotor.set(extenderOutput);
        SmartDashboard.putNumber("ExtEnc", m_extendEnc.getPosition());
        SmartDashboard.putNumber("ExtMotorDrive", extenderOutput);
        SmartDashboard.putNumber("ExtSetPoint", m_desiredArmPosition);
    }

    public void control(double rotation, double extend)
    {
        if (Math.abs(rotation) > Configuration.RotationDeadband)
        {
            /*
             * This sets the elevator desired set point. The PID controller
             * will then close the loop and move the elevator to the correct
             * position
             */
            m_desiredArmPosition += (rotation * Configuration.RotationScale);
            m_elevatorPID.setSetpoint(m_desiredArmPosition);
        }
        if (Math.abs(extend) > Configuration.ExtendDeadband)
        {
            /*
             * This sets the extend desired set point. The PID controller
             * will then close the loop and move the elevator to the correct
             * position
             */
            m_desiredArmPosition += (extend * Configuration.ElevatorScale);
            m_extenderPID.setSetpoint(m_desiredArmPosition);
        }
    }

    public Command MoveArmToLow()
    {
        // m_desiredArmPosition = -4;
        // return new PrintCommand("Low");
        return new InstantCommand(() -> m_desiredArmPosition = -4);
    }

    public Command MoveArmToMid()
    {
        // m_desiredArmPosition = -8;
        // return new PrintCommand("Mid");
        return new InstantCommand(() -> m_desiredArmPosition = -8);
    }

    public Command MoveArmToHigh()
    {
        // m_desiredArmPosition = -12;
        // return new PrintCommand("High");
        return new InstantCommand(() -> m_desiredArmPosition = -12);
    }
}
