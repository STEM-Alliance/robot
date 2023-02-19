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

        /*
         * TODO: Need to set the motor direction and speed.
         * Do we need to setup the limit switches?
         */

    }

    @Override

    public void periodic() {

    }

    public Command close() {
        // Close the grabber until we hit the limit switch
        return this.runOnce(() -> m_lMotor.set(TalonSRXControlMode.PercentOutput, 0.5));
    }

    public Command open() {
        // Open the grabber until we hit the limit switch
        return this.runOnce(() -> m_rMotor.set(TalonSRXControlMode.PercentOutput, 0.5));
    }

    public void slideGripper(double commandValue) {
        if (Math.abs(commandValue) > Configuration.GripperDeadband)
        {
            /*
             * Drive the motor directly.
             * TODO: Make sure these directions are correct
             */
            //m_lMotor.set(commandValue);
            //m_rMotor.set(commandValue);
        }
    }
}
