package frc.robot.SubSystems;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.Configuration;

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class ElevatorSubsystem extends SubsystemBase {
    private final CANSparkMax m_rotateMotor;
    private final PIDController m_elevatorPID = new PIDController(Configuration.ElevatorKp, Configuration.ElevatorKi, Configuration.ElevatorKd);
    private final RelativeEncoder m_rotateEnc;
    private double m_desiredArmPosition = 0;

    /** Creates a new DriveSubsystem. */
    public ElevatorSubsystem(int rotateMotorID) {
        // The SparkMax controls a Neo motor
        m_rotateMotor = new CANSparkMax(rotateMotorID, MotorType.kBrushless);
        // The Talon Controls a BAG motor
        m_rotateMotor.restoreFactoryDefaults();
        m_rotateMotor.setSmartCurrentLimit(Configuration.NeoLimit);

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
        var elevatorOutput = m_elevatorPID.calculate(m_rotateEnc.getPosition());
        m_rotateMotor.set(elevatorOutput);
        // SmartDashboard.putNumber("ArmEnc", m_rotateEnc.getPosition());
        // SmartDashboard.putNumber("ArmMotorDrive", elevatorOutput);
        // SmartDashboard.putNumber("ArmSetPoint", m_desiredArmPosition);
        SmartDashboard.putNumber("ArmCurrent", m_rotateMotor.getOutputCurrent());
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
            m_desiredArmPosition += (-rotation * Configuration.RotationScale);
            if (m_desiredArmPosition > 150)
            {
                m_desiredArmPosition = 40;
            }
            else if (m_desiredArmPosition < 0)
            {
                m_desiredArmPosition = 0;
            }

        }
        m_elevatorPID.setSetpoint(m_desiredArmPosition);
    }

    // public Command MoveArmToLow()
    // {
    //     return new InstantCommand(() -> m_desiredArmPosition = 18.6).andThen(new InstantCommand(() -> m_desiredExtPos = -0.45));
    // }

    // public Command MoveArmToMid()
    // {
    //     return new InstantCommand(() -> m_desiredArmPosition = 20);
    // }

    // public Command MoveArmToHigh()
    // {
    //     return new InstantCommand(() -> m_desiredArmPosition = 35);
    // }

    // public void UnlockExtend()
    // {
    //     m_desiredExtPos = 1;
    // }

    // public Command RetractHome()
    // {
    //     return new InstantCommand(() -> m_desiredExtPos = 0);
    // }

    // public boolean IsArmInPosition()
    // {
    //     if (m_rotateEnc.getPosition() >= m_desiredArmPosition)
    //     {
    //         return true;
    //     }
    //     return false;
    // }

    // public boolean IsArmExtendedInPosition()
    // {
    //     if (m_extendMotor.getSelectedSensorPosition() >= m_desiredExtPos)
    //     {
    //         return true;
    //     }
    //     return false;
    // }

    // public Command MoveArmToPosition()
    // {
    //     return new FunctionalCommand(() -> m_desiredArmPosition = Configuration.AutoArmPosition, () -> {}, interrupted -> {}, () -> IsArmInPosition());
    // }

    // public Command PlaceCone()
    // {
    //     return MoveArmToPosition();
    // }
}
