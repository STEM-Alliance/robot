package frc.robot.SubSystems;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class ElevatorSubsystem extends SubsystemBase {
    private final CANSparkMax m_rotateMotor;
    private final CANSparkMax m_extendMotor;
    public Object control;
    PIDController ElevatorPID = new PIDController(0, 0, 0);
    /** Creates a new DriveSubsystem. */
    public ElevatorSubsystem(int rotateMotorID, int extendMotorID) {
        m_rotateMotor = new CANSparkMax(rotateMotorID, MotorType.kBrushless);
        m_extendMotor = new CANSparkMax(extendMotorID, MotorType.kBrushless);
        m_rotateMotor.restoreFactoryDefaults();
        m_extendMotor.restoreFactoryDefaults();
        RelativeEncoder m_extendEnc = m_extendMotor.getEncoder();
        /*
         * TODO: Need to set the motor direction and speed.
         * Do we need to setup the limit switches?
         */

    }

    @Override

    public void periodic() {

    }
    // Void ElevatorCloseLoop(){
    //     m_rotateMotor.set(ElevatorPID.calculate(m_Enc.getPosition));
    // }
    // public Command low() {
    //     return new FunctionalCommand(ElevatorPID.setSetpoint(0);, , null, null, null)
    // }
        
    

    public void control(double Rotation, double Extend)
    {
        m_rotateMotor.set(Rotation);
        m_extendMotor.set(Extend);
        
    }
}
