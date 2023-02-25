package frc.robot.SubSystems;

import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.hal.*;

public class LEDSubsystem extends SubsystemBase {

    int m_red;
    int m_green;
    int m_blue;

    /** Creates a new DriveSubsystem. */
    public LEDSubsystem(int redChannel, int greenChannel, int blueChannel) {

        /*
         * TODO: Need to set the motor direction and speed.
         * Do we need to setup the limit switches?
         */
    }

    @Override

    public void periodic() {

    }

    public Command controlRed(boolean enable)
    {
        return this.runOnce(() -> System.out.println("Turning Red LED " + enable));
    }
}
