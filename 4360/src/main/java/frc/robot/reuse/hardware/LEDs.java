package frc.robot.reuse.hardware;

import edu.wpi.first.wpilibj.DriverStation.Alliance;

/** @author STEM Alliance of Fargo Moorhead */
public interface LEDs
{
    /** Turn the LEDs lights completely off */
    public void off();

    /** Get the attention of our robot's drive team (driver, operator) */
    public void signalDriveTeam();

    /** Get the attention of a human player near the field */
    public void signalHumanPlayer();

    /** Use the correct default color for robot Autonomous mode */
    public void setForAuto(Alliance team);

    /** Use the correct default color for robot Teleop mode */
    public void setForTeleop();

    /** Use the correct default color for the mode the robot is currently in (Autonomous or Teleop) */
    public void useRobotModeColor();

    /** Scroll through colors used by this Robot */
    public boolean testRobotSpecificColors();

    /** Scroll through each color */
    public boolean testScrollAll();

    /** Indicate whether the robot functional tests all passed */
    public void signalFunctionalTestResult(boolean testsPassed);
}
