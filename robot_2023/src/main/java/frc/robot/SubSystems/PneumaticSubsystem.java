package frc.robot.SubSystems;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.util.function.BooleanSupplier;

public class PneumaticSubsystem extends SubsystemBase {

    Compressor m_pcm;
    DoubleSolenoid m_extend = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 2, 3);
    DoubleSolenoid m_gripper = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 4, 5);
    DoubleSolenoid m_deployHDrive = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);

    boolean m_toggle = false;

    /** Creates a new DriveSubsystem. */
    public PneumaticSubsystem(int pcmID) {
        // The SparkMax controls a Neo motor
        m_pcm = new Compressor(pcmID, PneumaticsModuleType.CTREPCM);

        m_pcm.enableDigital();

        m_extend.set(Value.kReverse);
        m_gripper.set(Value.kForward);
        m_deployHDrive.set(Value.kForward);
    }

    @Override

    public void periodic()
    {
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
       return new InstantCommand(() -> m_deployHDrive.toggle());
    }

    public Command openClaw()
    {
        return new InstantCommand(() -> m_gripper.set(Value.kReverse));
    }

    public Command closeClaw()
    {
        return new InstantCommand(() -> m_gripper.set(Value.kForward));
    }

    public Command extendArm(BooleanSupplier armPos)
    {
        return new ConditionalCommand(new InstantCommand(() -> m_extend.set(Value.kForward)), new InstantCommand(), armPos);
        //return new InstantCommand(() -> m_extend.set(Value.kForward));
    }

    public Command retractArm()
    {
        return new InstantCommand(() -> m_extend.set(Value.kReverse));
    }

    public void deployHDrive()
    {
        m_deployHDrive.set(Value.kReverse);
    }

    public void retractHDrive()
    {
        m_deployHDrive.set(Value.kForward);
    }

}
