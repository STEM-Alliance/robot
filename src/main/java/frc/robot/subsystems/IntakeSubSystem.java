package frc.robot.subsystems;

import frc.robot.Configuration;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

public class IntakeSubSystem extends SubsystemBase {
    public final CANSparkMax m_intake;
    public final CANSparkMax m_shooter_1;
    // public final CANSparkMax m_shooter_2;
    public final CANSparkMax m_midintake;
    private boolean IntakeRunning;

    private double m_rotateSpeed = 0;
    /** Creates a new DriveSubsystem. */
    public IntakeSubSystem(int intakeMotorID, int shooterMotorID_1, int midtakeMotorID) {
        m_intake = new CANSparkMax(intakeMotorID, MotorType.kBrushless);
        m_shooter_1 = new CANSparkMax(shooterMotorID_1, MotorType.kBrushless);
        // m_shooter_2 = new CANSparkMax(shooterMotorID_2, MotorType.kBrushless);
        m_midintake = new CANSparkMax(midtakeMotorID, MotorType.kBrushless);

        m_intake.setSmartCurrentLimit(Configuration.Neo550Limit);
        m_shooter_1.setSmartCurrentLimit(30);
        // m_shooter_2.setSmartCurrentLimit(30);
        m_midintake.setSmartCurrentLimit(25);

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
        m_intake.set(1);
        m_midintake.set(.1);
        return new InstantCommand();
    }


    public Command doneLoading()
    {
        m_intake.set(0);
        return new InstantCommand();        
    }

    public Command grabnNoteDone()
    {
        // grabNote().end(true);
        return new InstantCommand();
    }

    public Command shootNote()
    {
        return new InstantCommand();
    }
}
