package frc.robot.SubSystems;

import frc.robot.Configuration;
import edu.wpi.first.wpilibj2.command.*;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

public class GripperSubsystem extends SubsystemBase {
    private final TalonSRX m_lMotor;
    private final TalonSRX m_rMotor;

    /** Creates a new DriveSubsystem. */
    public GripperSubsystem(int leftMotorCanID, int rightMotorCanID) {
        m_lMotor = new TalonSRX(leftMotorCanID);
        m_rMotor = new TalonSRX(rightMotorCanID);
        m_lMotor.configFactoryDefault();
        m_rMotor.configFactoryDefault();

        m_lMotor.configPeakCurrentLimit(Configuration.M775ProLimit);
        m_rMotor.configPeakCurrentLimit(Configuration.M775ProLimit);

        /*
         * TODO: Need to set the motor direction and speed.
         * Do we need to setup the limit switches?
         */

    }

    @Override

    public void periodic() {

    }

    private void controlMotor(TalonSRX motor, double commandValue)
    {
        if (Math.abs(commandValue) > Configuration.GripperDeadband)
        {
            /*
             * Not sure if this control scheme is going to work well.
             * But the idea is you could use the two joysticks to move the
             * grippers independently of each other
             */
            motor.set(TalonSRXControlMode.PercentOutput, commandValue);
        }
    }

    public void slideGripper(double leftCommand, double rightCommand)
    {
        controlMotor(m_lMotor, leftCommand);
        controlMotor(m_rMotor, rightCommand);
    }
}
