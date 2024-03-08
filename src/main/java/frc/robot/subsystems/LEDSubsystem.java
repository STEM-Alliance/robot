package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj.PWM;

public class LEDSubsystem extends SubsystemBase {

    private PWM m_leds = new PWM(1);

    /** Creates a new DriveSubsystem. */
    public LEDSubsystem() 
    {
        m_leds.setSpeed(0.61);
    }

    @Override

    public void periodic() {

    }

    public Command red()
    {
        return new InstantCommand(() -> setRed());
    }

    public Command yellow()
    {
        return new InstantCommand(() -> setYellow());
    }

    public Command blue()
    {
        return new InstantCommand(() -> setBlue());
    }

    public Command crazy()
    {
        return new InstantCommand(() -> setCrazy());
    }

    public void setRed() {
        m_leds.setSpeed(0.61);
    }

    public void setYellow() {
        m_leds.setSpeed(0.69);
    }

    public void setBlue() {
        m_leds.setSpeed(0.91);
    }

    public void setCrazy() {
        m_leds.setSpeed(-0.59);
    }
}
