package frc.robot.subsystems;

import frc.robot.Configuration;
import edu.wpi.first.wpilibj2.command.*;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

public class IntakeSubSystem extends SubsystemBase {
    private final CANSparkMax m_intake;
    private final CANSparkMax m_shooter;

    private double m_rotateSpeed = 0;

    /** Creates a new DriveSubsystem. */
    public IntakeSubSystem(int intakeMotorID, int shooterMotorID) {
        m_intake = new CANSparkMax(intakeMotorID, MotorType.kBrushless);
        m_shooter = new CANSparkMax(shooterMotorID, MotorType.kBrushless);

        m_intake.setSmartCurrentLimit(Configuration.Neo550Limit);
        m_shooter.setSmartCurrentLimit(Configuration.Neo550Limit);

        /*
         * TODO: Need to set the motor direction and speed.
         * Do we need to setup the limit switches?
         */

    }

    @Override

    public void periodic() {
        // Things we need to do on a periodic basis
        //System.out.println("Gripper period")
    }

    public void doStuff() {
        // Do stuff
    }

    public Command grabNote()
    {
        //return this.startEnd(() -> {}, () -> m_gripperState = GripperState.IDLE);
        return Commands.run(() -> doStuff());
    }
}
