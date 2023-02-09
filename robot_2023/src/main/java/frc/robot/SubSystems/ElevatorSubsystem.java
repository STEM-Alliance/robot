package frc.robot.SubSystems;

import edu.wpi.first.wpilibj2.command.*;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class ElevatorSubsystem extends SubsystemBase {
    private final CANSparkMax m_rotateMotor;
    private final CANSparkMax m_extendMotor;

    /** Creates a new DriveSubsystem. */
    public ElevatorSubsystem(int rotateMotorID, int extendMotorID) {
        m_rotateMotor = new CANSparkMax(rotateMotorID, MotorType.kBrushless);
        m_extendMotor = new CANSparkMax(extendMotorID, MotorType.kBrushless);
        m_rotateMotor.restoreFactoryDefaults();
        m_extendMotor.restoreFactoryDefaults();

        /*
         * TODO: Need to set the motor direction and speed.
         * Do we need to setup the limit switches?
         */

    }

    @Override

    public void periodic() {

    }

    public void control(double rotation, double extend)
    {
        m_rotateMotor.set(rotation);
        m_extendMotor.set(extend);
    }
}
