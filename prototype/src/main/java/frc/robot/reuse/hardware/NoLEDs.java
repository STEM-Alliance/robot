package frc.robot.reuse.hardware;

import edu.wpi.first.wpilibj.DriverStation.Alliance;

/** Use this class if your robot doesn't have physical LED's connected */
public class NoLEDs implements LEDs
{
    public void off()
    {
    }

    public void signalDriveTeam()
    {
    }

    public void signalHumanPlayer()
    {
    }

    public void setForAuto(Alliance team)
    {
    }

    public void setForTeleop()
    {
    }

    public void useRobotModeColor()
    {
    }

    public boolean testRobotSpecificColors()
    {
        return false;
    }

    public boolean testScrollAll()
    {
        return false;
    }

    public void signalFunctionalTestResult(boolean testsPassed)
    {
    }
}
