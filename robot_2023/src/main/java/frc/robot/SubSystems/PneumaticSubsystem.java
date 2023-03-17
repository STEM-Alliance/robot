package frc.robot.SubSystems;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.Configuration;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class PneumaticSubsystem extends SubsystemBase {

    Compressor m_pcm;
    DoubleSolenoid m_extend = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);
    DoubleSolenoid m_gripper = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 2, 3);
    DoubleSolenoid m_deployHDrive = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 4, 5);

    boolean m_toggle = false;

    /** Creates a new DriveSubsystem. */
    public PneumaticSubsystem(int pcmID) {
        // The SparkMax controls a Neo motor
        m_pcm = new Compressor(pcmID, PneumaticsModuleType.CTREPCM);

        m_pcm.enableDigital();

        m_extend.set(Value.kForward);
        m_gripper.set(Value.kForward);
        m_deployHDrive.set(Value.kForward);
    }

    @Override

    public void periodic()
    {
        if (m_toggle)
        {
            m_toggle = false;
            m_extend.toggle();
            m_gripper.toggle();
            m_deployHDrive.toggle();
            System.out.println("toggle");
        }
    }

    public Command toggleExtend()
    {
        return new InstantCommand(() -> m_extend.toggle());
    }

    public Command toggleGripper()
    {
        return new InstantCommand(() -> m_gripper.toggle());
    }

    public Command toggleHDrive()
    {
       return new InstantCommand(() -> m_toggle = true);
    }
}
