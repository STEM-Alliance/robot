package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj.PWM;

public class LEDSubsystem extends SubsystemBase {

    private PWM m_leds = new PWM(1);
    private double m_flashingColor;
    private boolean m_flashingEnabled;
    private int m_flashingCounter = 0;
    private Command m_flashGreenCmd;

    /** Creates a new DriveSubsystem. */
    public LEDSubsystem() 
    {
        m_leds.setSpeed(0.61);
        m_flashGreenCmd = flashGreen();
    }

    @Override

    public void periodic() {
        if (m_flashingEnabled) {
            m_flashingCounter++;
            if (m_flashingCounter >= 25) {
                // This is black
                m_leds.setSpeed(0.99);
            }
            else if (m_flashingCounter >= 50) {
                m_leds.setSpeed(m_flashingColor);
                m_flashingCounter = 0;
            }
        }
    }

    private void resetFlashing() {
        m_flashingColor = 0.99;
        m_flashingCounter = 0;
        m_flashingEnabled = false;
    }

    public Command flashGreen() {
        return new InstantCommand(() -> setGreen()).andThen(
        new WaitCommand(0.1).andThen(
        new InstantCommand(() -> setBlack()).andThen(
        new WaitCommand(0.1))));
    }

    public Command red()
    {
        return new InstantCommand(() -> setRed());
    }

    public Command green()
    {
        return new InstantCommand(() -> setGreen());
    }

    public Command flashingGreen()
    {
        return new InstantCommand(() -> setFlashingGreen());
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
        resetFlashing();
    }

    public void setGreen() { 
        m_leds.setSpeed(0.61);
        resetFlashing();
    }

    public void setFlashingGreen() {
        m_flashingColor = 0.75;
        m_flashingEnabled = true;
        m_leds.setSpeed(0.75);
    }

    public void setYellow() {
        m_leds.setSpeed(0.69);
        resetFlashing();
    }

    public void setBlue() {
        m_leds.setSpeed(0.91);
        resetFlashing();
    }

    public void setCrazy() {
        m_leds.setSpeed(-0.59);
        resetFlashing();
    }

    public void setBlack() {
        m_leds.setSpeed(0.99);
        resetFlashing();
    }
}
