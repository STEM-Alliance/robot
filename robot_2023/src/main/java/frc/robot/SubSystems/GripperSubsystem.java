package frc.robot.SubSystems;

import edu.wpi.first.wpilibj2.command.*;
import com.revrobotics.*;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class GripperSubsystem extends SubsystemBase {
    private final CANSparkMax m_lMotor;
    private final CANSparkMax m_rMotor;

    /** Creates a new DriveSubsystem. */
    public GripperSubsystem(int leftMotorCanID, int rightMotorCanID) {
        m_lMotor = new CANSparkMax(leftMotorCanID, MotorType.kBrushless);
        m_rMotor = new CANSparkMax(rightMotorCanID, MotorType.kBrushless);
        m_lMotor.restoreFactoryDefaults();
        m_rMotor.restoreFactoryDefaults();

        /*
         * TODO: Need to set the motor direction and speed.
         * Do we need to setup the limit switches?
         */

    }

    @Override

    public void periodic() {

    }

    public CommandBase close() {
        return this.runOnce(() -> System.out.println("Close Grabber"));
    }

    public CommandBase open() {
        return this.runOnce(() -> System.out.println("Open Grabber"));
    }

}
