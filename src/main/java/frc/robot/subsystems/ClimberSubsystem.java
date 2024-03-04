package frc.robot.subsystems;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Configuration;

public class ClimberSubsystem extends SubsystemBase {
    CANSparkMax m_climbMotor;

    /** Creates a new ClimberSubsystem. */
    public ClimberSubsystem(int climbMotor) {
        m_climbMotor = new CANSparkMax(climbMotor, MotorType.kBrushless);
        m_climbMotor.setSmartCurrentLimit(Configuration.Neo550Limit);
    }

    public void runClimber(double setpoint) {
        m_climbMotor.set(setpoint);
    }

    // public Command climbUp() {
    //     return new FunctionalCommand(
    //         () -> {},
    //         {},
    //         interrupted -> {},
    //         {},
    //         this);
    // }


    // // public Command climbUp() {
    // //     return new FunctionalCommand(
    // //         () -> m_climbMotor.set(0),
    // //         () -> {},
    // //         interrupted -> m_climbMotor.set(0),
    // //         () -> m_climbMotor.set(0.5),
    // //         this);
    // // }

    // public Command climbDown() {
    // return new FunctionalCommand(
    //     () -> m_climbMotor.set(0),
    //     () -> {},
    //     interrupted -> m_climbMotor.set(0),
    //     () -> m_climbMotor.set(-0.5),
    //     this);
    // }
}